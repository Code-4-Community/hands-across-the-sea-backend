import os
import requests

# The directory that contains all `.properties.example` files
PROPERTIES_DIR = "common/src/main/resources/properties/"

# The environment variable with the Slack webhook URL
SLACK_WEBHOOK_ENV_VAR = "SLACK_WEBHOOK_URL"

# Dictionary that maps example file names to a dictionary where the keys
#   are the PROPERTIES (KEYS), and the values are ENVIRONMENT VARIABLES
ENV_VALUES = {
    "server.properties.example": {

        # AWS Properties
        "aws_access_key": "AWS_ACCESS_KEY_ID",
        "aws_secret_key": "AWS_SECRET_ACCESS_KEY",
#         "aws_s3_bucket_url": "AWS_S3_BUCKET_URL",
#         "aws_s3_bucket_name": "AWS_S3_BUCKET_NAME",
#         "aws_s3_upload_dir": "AWS_S3_BUCKET_DIR",

        # Database Properties
        "database_url": "DB_DOMAIN",
        "database_username": "DB_USERNAME",
        "database_password": "DB_PASSWORD",

#         # Email Properties
#         "email_send_password": "GMAIL_APP_PASSWORD",
#         "email_should_send": "GMAIL_APP_ENABLED",

        # JWT Properties
        "jwt_secret_key": "JWT_SECRET_KEY",

        # Slack Properties
        "slack_webhook_url": SLACK_WEBHOOK_ENV_VAR,
        "slack_enabled": "SLACK_ENABLED",
    },
}

# Whether or not to log errors to Slack
SEND_SLACK = True


def main():
    global SEND_SLACK

    # Enable logging deploy errors to Slack if the webhook url is set in the environment
    SEND_SLACK = True if SLACK_WEBHOOK_ENV_VAR in os.environ else False

    # Iterate through every item in the given directory
    dir_contents = os.listdir(PROPERTIES_DIR)
    for file_name in dir_contents:
        # Only copy/modify .properties.example files
        if not file_name.endswith(".properties.example"):
            continue

        print("Reading:", file_name)

        out_file_name = file_name[:-8]  # Strips the ".example" from the file name
        print("Writing:", out_file_name)

        with open(PROPERTIES_DIR + out_file_name, "w") as file_properties:
            with open(PROPERTIES_DIR + file_name, "r") as file_example:
                process_properties(file_name, file_example, file_properties)


def process_properties(example_file_name, file_example, file_properties):
    """
    Given the name of an example file and IO files for reading and writing, copies
    the contents of the example file to the properties file. All placeholder
    values present in ENV_VALUES are replaced with their respective values.

    :param example_file_name: the name of the example file.
    :param file_example: the IO file to read with example properties.
    :param file_properties: the IO file to write the production properties.
    :return: void
    """
    # If no replacement values are provided, copy the .example file as-is
    if example_file_name not in ENV_VALUES:
        for line in file_example:
            file_properties.write(line)
        return

    # Key: property, value: environment variable
    replacement_dict = ENV_VALUES[example_file_name]

    # Otherwise, copy and replace the placeholders with environment variables
    for line in file_example:
        line_split = line.split("=", 1)

        # Copy the whole line if it did not split correctly
        if len(line_split) != 2:
            file_properties.write(line)
            continue

        # Copy the the whole line if no replacement var is provided
        key_placeholder = line_split[0].strip()  # The key before the "="
        if key_placeholder not in replacement_dict:
            file_properties.write(line)
            continue

        # Else, copy the line and replace the placeholder
        if key_placeholder in replacement_dict:
            env_var = replacement_dict[key_placeholder]
            env_var = env_var if not env_var.startswith("$") else env_var[1:]  # Strip leading "$"

            # Get the value of the environment variable
            out_value = os.environ.get(env_var)

            # Ensure the desired environment variable is actually set
            if out_value is None:
                out_value = "${}".format(env_var)  # Set value to environment variable name
                error_msg = "`[{}]`: Replacing empty environment variable: `${}`".format(example_file_name, env_var)
                handle_error(error_msg)

            out_line = "{} = {}\n".format(key_placeholder, out_value)
            file_properties.write(out_line)
            continue


def handle_error(error_msg):
    """
    Handles printing error messages and sending them to Slack, if applicable.

    :param error_msg: the error message to log.
    :return: void
    """
    print(error_msg)

    if SEND_SLACK:
        try:
            webhook_url = os.environ.get(SLACK_WEBHOOK_ENV_VAR)
            payload = {
                "text": error_msg
            }
            requests.post(webhook_url, json=payload)
        except Exception as e:
            print("Failed to send Slack alert:", e)


main()
