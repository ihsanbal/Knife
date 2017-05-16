#
#           Circle CI & gradle.properties live in harmony
# 
# Android convention is to store your API keys in a local, non-versioned
# gradle.properties file. Circle CI doesn't allow users to upload pre-populated
# gradle.properties files to store this secret information, but instead allows
# users to store such information as environment variables.
#
# This script creates a local gradle.properties file on current the Circle CI
# instance. It then reads environment variable TEST_API_KEY_ENV_VAR which a user
# has defined in their Circle CI project settings environment variables, and 
# writes this value to the Circle CI instance's gradle.properties file.
# 
# You must execute this script via your circle.yml as a pre-process dependency,
# so your gradle build process has access to all variables.
#
#   dependencies:
#       pre:
#        - source firebaseSetups.sh && copyFirebaseVarsToPlayFolder

#!/usr/bin/env bash

function copyFirebaseVarsToPlayFolder {
    KNIFE_PATH=$HOME"/Knife/.play/knife.json"
    echo "$KNIFE_PATH"

    if [ ! -f "$KNIFE_PATH" ]; then
        echo "Knife Json file does not exist"
    fi
        echo "Creating Knife Json file..."
        touch ${KNIFE_PATH}

        echo "Writing json to knife.json"
                echo "$KNIFE_JSON" >> ${KNIFE_PATH}
    
    printf "%s" "$(<${KNIFE_PATH})"
}
