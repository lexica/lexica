#!/bin/bash

DIR=$(dirname "$0")
BUILD_DIR="$DIR/build"
DICT_FILE="$DIR/dictionary.de_DE.txt"
LICENSE_FILE="$DIR/dictionary.de_DE.LICENSE"

if [ -d "$BUILD_DIR" ]
then
    echo "Build direectory $BUILD_DIR is not empty. Please remove first."
    exit 1
fi

echo "Fetching German wordlist..."
git clone --depth=1 --quiet https://github.com/enz/german-wordlist "$BUILD_DIR"

echo "Lowercasing wordlist and blacklist..."
cat "$BUILD_DIR/blacklist" | tr '[:upper:]' '[:lower:]' | sort | uniq > "$BUILD_DIR/remove"
cat "$BUILD_DIR/words"     | tr '[:upper:]' '[:lower:]' | sort | uniq > "$BUILD_DIR/keep"

echo "Removing blacklist words, loanwords, and dumping dictionary to $DICT_FILE..."
grep --line-regexp --invert-match --file="$BUILD_DIR/remove" "$BUILD_DIR/keep" \
  | awk 'length($0) < 10 && length($0) > 2' \
  | grep -v -P "[àáâåÅçčéèêēëīíïîłñōóõœŒšŠūûú]" \
  | sed 's/ß/ss/g' \
  > "$DICT_FILE"

echo "Copying license from source repo..."
cp "$BUILD_DIR/COPYING" "$LICENSE_FILE"

echo "Removing build directory '$BUILD_DIR'..."
rm -rf "$BUILD_DIR"
