<p align="center">
    <img src="https://github.com/ihsanbal/Knife/blob/master/app/src/main/ic_launcher-web.png" width="200" height="200"/>
</p>

# Knife for Twitter - [![CircleCI](https://circleci.com/gh/ihsanbal/Knife/tree/master.svg?style=svg)](https://circleci.com/gh/ihsanbal/Knife/tree/master) ![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=square)
You can now share without considering the 140 character limit. Knife application allows you to split the text into meaningful 140-character tweets and share it with one button with different options.
<p align="center">
        <a href="https://play.google.com/store/apps/details?id=com.ihsanbal.knife"> <img src="https://github.com/ihsanbal/Knife/blob/master/img/google-play-badge.png" width="406" height="150"> </a>
</p>

# contributor are welcome
Knife application building on circleci with three different script. Writing Environment variables to files.
Help me to build new features and translate.

<p align="center">
        <a href="https://play.google.com/store/apps/details?id=com.ihsanbal.knife"> <img src="https://github.com/ihsanbal/Knife/blob/development/img/enviroment_variables.png" width="606" height="350"> </a>
</p>

Sample script for [playServiceSetups.sh](https://github.com/ihsanbal/Knife/blob/master/firebaseSetups.sh)

```shell
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
}
```

Screenshots
--------

<p align="center">
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230045.png" width="100" height="180"/>
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230153.png" width="100" height="180"/>
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230358.png" width="100" height="180"/>
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230431.png" width="100" height="180"/>
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230602.png" width="100" height="180"/>
    <img src="https://github.com/ihsanbal/Knife/blob/master/img/device-2017-05-31-230819.png" width="100" height="180"/>
</p>
