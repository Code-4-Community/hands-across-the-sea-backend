#!/bin/sh

# CD into properties directory
cd "$1" || exit 1

# Copy each .example file to a .properties file
for EX_FILE in *.properties.example; do
  PROP_FILE="$(echo "$EX_FILE" | cut -d "." -f 1).properties"
  echo "Copying $EX_FILE to $PROP_FILE"
  cp "$EX_FILE" "$PROP_FILE"
done
