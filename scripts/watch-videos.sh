#!/bin/bash

SOURCE_FILE=${1:-/dev/stdin}

while read URL
do
    echo "Opening Chrome with: $URL"
    google-chrome "$URL"
    sleep 15
done < $SOURCE_FILE
