#!/bin/bash

#Needed to have a deterministic outcome regarding the sort order. Otherwise dependant on the computer settings. 
export LC_ALL=en_US.utf8

DIR=$(dirname "$0")
BUILD_DIR="$DIR/build"
DICT_FILE="$DIR/dictionary.de_DE.txt"
LICENSE_FILE="$DIR/dictionary.de_DE.LICENSE"

if [ -d "$BUILD_DIR" ]
then
    echo "Build directory $BUILD_DIR is not empty. Please remove first."
    exit 1
fi

echo "Fetching German wordlist..."
git clone --depth=1 --quiet https://github.com/enz/german-wordlist "$BUILD_DIR"

echo "Correcting words (i.a. lowering) and dumping dictionary to $DICT_FILE..."
cat "$BUILD_DIR/words" | tr '[:upper:]' '[:lower:]' | sort | uniq \
  | awk 'length($0) < 10 && length($0) > 2' \
  | grep -v -P "[àáâåÅçčéèêēëīíïîłñōóõœŒšŠūûú]" \
  | sed 's/ß/ss/g' \
  > "$DICT_FILE"

echo "Copying license from source repo..."
cp "$BUILD_DIR/COPYING" "$LICENSE_FILE"

echo "Removing build directory '$BUILD_DIR'..."
rm -rf "$BUILD_DIR"
