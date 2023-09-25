#!/bin/bash
set -e

#Needed to have a deterministic outcome regarding the sort order. Otherwise dependant on the computer settings. 
export LC_ALL=fi_FI.utf8

DIR=$(dirname "$0")
BUILD_DIR="$DIR/build"
DICT_FILE="$DIR/dictionary.fi.txt"

if [ -d "$BUILD_DIR" ]
then
    echo "Build directory $BUILD_DIR is not empty. Please remove first."
    exit 1
fi

mkdir "$BUILD_DIR"

echo "Fetching Finnish wordlist..."
curl -L -o "$BUILD_DIR/words.csv" https://kaino.kotus.fi/lataa/nykysuomensanalista2022.csv

echo "Lowercasing and filtering words and dumping dictionary to $DICT_FILE..."
cat "$BUILD_DIR/words.csv" | tail -n +2 | cut -f1 \
  | tr '[:upper:]' '[:lower:]' | sort | uniq \
  | grep -P '^[abdefghijklmnoprstuvyäö]{3,9}$' \
  > "$DICT_FILE"

echo "Removing build directory '$BUILD_DIR'..."
rm -rf "$BUILD_DIR"

