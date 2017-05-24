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
#        - source environmentSetup.sh && copyEnvVarsToGradleProperties

#!/usr/bin/env bash

function copyEnvVarsToGradleProperties {
    GRADLE_PROPERTIES=$HOME"/.gradle/gradle.properties"

    if [ ! -f "$GRADLE_PROPERTIES" ]; then
        echo "Gradle Properties does not exist"
    fi
        echo "Creating Gradle Properties file..."
        touch ${GRADLE_PROPERTIES}

        echo "Writing STORE_PASSWORD to gradle.properties"
                echo "STORE_PASSWORD=$STORE_PASSWORD" >> ${GRADLE_PROPERTIES}

        echo "Writing KEY_ALIAS to gradle.properties..."
                echo "KEY_ALIAS=$KEY_ALIAS" >> ${GRADLE_PROPERTIES}

        echo "Writing KEY_PASSWORD to gradle.properties..."
                echo "KEY_PASSWORD=$KEY_PASSWORD" >> ${GRADLE_PROPERTIES}

        echo "Writing FABRIC_KEY to gradle.properties..."
                echo "FABRIC_KEY=$FABRIC_KEY" >> ${GRADLE_PROPERTIES}

        echo "Writing RSA_KEY to gradle.properties..."
                echo "RSA_KEY=$RSA_KEY" >> ${GRADLE_PROPERTIES}

        echo "Writing TWITTER_KEY to gradle.properties..."
                echo "TWITTER_KEY=$TWITTER_KEY" >> ${GRADLE_PROPERTIES}

        echo "Writing TWITTER_SECRET to gradle.properties..."
                echo "TWITTER_SECRET=$TWITTER_SECRET" >> ${GRADLE_PROPERTIES}

        echo "Writing ADMOB_APP_ID to gradle.properties..."
                echo "ADMOB_APP_ID=$ADMOB_APP_ID" >> ${GRADLE_PROPERTIES}

        echo "Writing AD_UNIT_ID_INTERSTITIAL to gradle.properties..."
                echo "AD_UNIT_ID_INTERSTITIAL=$AD_UNIT_ID_INTERSTITIAL" >> ${GRADLE_PROPERTIES}

        echo "Writing AD_UNIT_ID_BANNER to gradle.properties..."
                echo "AD_UNIT_ID_BANNER=$AD_UNIT_ID_BANNER" >> ${GRADLE_PROPERTIES}
}
