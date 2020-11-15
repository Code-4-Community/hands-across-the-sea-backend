package com.codeforcommunity.requester;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.codeforcommunity.aws.EncodedImage;
import com.codeforcommunity.exceptions.BadRequestImageException;
import com.codeforcommunity.exceptions.S3FailedUploadException;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class S3Requester {
  private static final String BUCKET_PUBLIC_URL;
  private static final String BUCKET_PUBLIC;
  private static final String DIR_PUBLIC;

  private static final AmazonS3 s3Client;

  static {
    BUCKET_PUBLIC_URL = PropertiesLoader.loadProperty("aws_s3_bucket_url");
    BUCKET_PUBLIC = PropertiesLoader.loadProperty("aws_s3_bucket_name");
    DIR_PUBLIC = PropertiesLoader.loadProperty("aws_s3_upload_dir");

    s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
  }

  /**
   * Validates whether or not the given String is a base64 encoded image in the following format:
   * data:image/{extension};base64,{imageData}.
   *
   * @param base64Image the potential encoding of the image.
   * @return null if the String is not an encoded base64 image, otherwise an {@link EncodedImage}.
   */
  private static EncodedImage validateBase64Image(String base64Image) {
    // Expected Base64 format: "data:image/{extension};base64,{imageData}"

    if (base64Image == null || base64Image.length() < 10) {
      return null;
    }

    String[] base64ImageSplit = base64Image.split(",", 2); // Split the metadata from the image data
    if (base64ImageSplit.length != 2) {
      return null;
    }

    String meta = base64ImageSplit[0]; // The image metadata (e.g. "data:image/png;base64")
    String[] metaSplit = meta.split(";", 2); // Split the metadata into data type and encoding type

    if (metaSplit.length != 2 || !metaSplit[1].equals("base64")) {
      // Ensure the encoding type is base64
      return null;
    }

    String[] dataSplit = metaSplit[0].split(":", 2); // Split the data type
    if (dataSplit.length != 2) {
      return null;
    }

    String[] data = dataSplit[1].split("/", 2); // Split the image type here (e.g. "image/png")
    if (data.length != 2 || !data[0].equals("image")) {
      // Ensure the encoded data is an image
      return null;
    }

    String fileExtension = data[1]; // The image type (e.g. "png")
    String encodedImage = base64ImageSplit[1]; // The encoded image

    return new EncodedImage("image", fileExtension, encodedImage);
  }

  /**
   * Validate the given base64 encoding of an image and upload it to the LLB public S3 bucket.
   *
   * @param fileName the desired name of the new file in S3 (without a file extension).
   * @param directoryName the desired directory of the file in S3 (without leading or trailing '/').
   * @param base64Encoding the base64 encoding of the image to upload.
   * @return null if the encoding fails validation and image URL if the upload was successful.
   * @throws BadRequestImageException if the base64 image validation or image decoding failed.
   * @throws S3FailedUploadException if the upload to S3 failed.
   */
  private static String validateBase64ImageAndUploadToS3(
      String fileName, String directoryName, String base64Encoding)
      throws BadRequestImageException, S3FailedUploadException {
    if (base64Encoding == null) {
      // No validation/uploading required if given no data
      return null;
    }

    EncodedImage encodedImage = validateBase64Image(base64Encoding);
    if (encodedImage == null) {
      // Image failed to validate
      throw new BadRequestImageException();
    }

    String fullFileName = String.format("%s.%s", fileName, encodedImage.getFileExtension());
    File tempFile;

    try {
      // Temporarily writes the image to disk to decode
      byte[] imageData = Base64.getDecoder().decode(encodedImage.getBase64ImageEncoding());
      tempFile = File.createTempFile(fullFileName, null, null);
      FileOutputStream fos = new FileOutputStream(tempFile);
      fos.write(imageData);
      fos.flush();
      fos.close();
    } catch (IllegalArgumentException | IOException e) {
      // The image failed to decode
      throw new BadRequestImageException();
    }

    // Create the request to upload the image
    PutObjectRequest awsRequest =
        new PutObjectRequest(BUCKET_PUBLIC, directoryName + "/" + fullFileName, tempFile);

    // Set the image to be publicly available
    awsRequest.setCannedAcl(CannedAccessControlList.PublicRead);

    // Set the image file metadata (to be of type image)
    ObjectMetadata awsObjectMetadata = new ObjectMetadata();
    awsObjectMetadata.setContentType(encodedImage.getFileType() + encodedImage.getFileExtension());
    awsRequest.setMetadata(awsObjectMetadata);

    try {
      // Perform the upload to S3
      s3Client.putObject(awsRequest);
    } catch (SdkClientException e) {
      // The AWS S3 upload failed
      throw new S3FailedUploadException(e.getMessage());
    }

    // Delete the temporary file that was written to disk
    tempFile.delete();

    return String.format("%s/%s/%s", BUCKET_PUBLIC_URL, directoryName, fullFileName);
  }

  /**
   * Validate the given base64 encoding of an image and upload it to the LLB public S3 bucket for
   * Events.
   *
   * @param eventTitle the title of the Event.
   * @param base64Encoding the encoded image to upload.
   * @return null if the initial base64Encoding was null, or the image URL if the upload was
   *     successful.
   * @throws BadRequestImageException if the base64 decoding failed.
   * @throws S3FailedUploadException if the upload to S3 failed.
   */
  public static String validateUploadImageToS3LucyEvents(String eventTitle, String base64Encoding)
      throws BadRequestImageException, S3FailedUploadException {
    String fileName = getImageFileNameWithoutExtension(eventTitle);
    return validateBase64ImageAndUploadToS3(fileName, DIR_PUBLIC, base64Encoding);
  }

  /**
   * Removes special characters, replaces spaces, and appends "_thumbnail".
   *
   * @param eventTitle the title of the event.
   * @return the String for the image file name (without the file extension).
   */
  public static String getImageFileNameWithoutExtension(String eventTitle) {
    String title =
        eventTitle.replaceAll(
            "[!@#$%^&*()=+./\\\\|<>`~\\[\\]{}?]", ""); // Remove special characters
    return title.replace(" ", "_").toLowerCase()
        + "_thumbnail"; // The desired name of the file in S3
  }
}
