#!/bin/bash

URL_FILE=${1:-nas-app-videos-urls}
ID_FILE="youtube-video-ids"
API_KEY=""

rm $ID_FILE
cat $URL_FILE | cut -d'/' -f5 | cut -d'?' -f1 >> $ID_FILE

BATCH_COUNTER=0
BATCH=""
while read -r LINE
do
    ID=$LINE
    if [[ $BATCH_COUNTER == 50 ]]; then
        VIDEO_IDS=${BATCH:1}
        YOUTUBE_API_URL="https://www.googleapis.com/youtube/v3/videos?key=$API_KEY&part=contentDetails&id=$VIDEO_IDS"
        curl "$YOUTUBE_API_URL" 
        BATCH_COUNTER=0
        BATCH=""
    else
        BATCH_COUNTER=$((BATCH_COUNTER + 1))
        BATCH="$BATCH,$ID"
    fi
done < "$ID_FILE"
