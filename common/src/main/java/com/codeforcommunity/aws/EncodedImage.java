package com.codeforcommunity.aws;

public class EncodedImage {
  private String fileType;
  private String fileExtension;
  private String base64ImageEncoding;

  public EncodedImage(String fileType, String fileExtension, String base64ImageEncoding) {
    this.fileType = fileType;
    this.fileExtension = fileExtension;
    this.base64ImageEncoding = base64ImageEncoding;
  }

  public String getFileType() {
    return fileType;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String getBase64ImageEncoding() {
    return base64ImageEncoding;
  }
}
