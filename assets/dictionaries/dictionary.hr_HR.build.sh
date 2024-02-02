#!/bin/bash

#Needed to have a deterministic outcome regarding the sort order. Otherwise dependant on the computer settings. 
export LC_ALL=en_US.utf8

DIR=$(dirname "$0")
BUILD_DIR="$DIR/build.hr_HR"
RAW_WORDLIST_FILE="$BUILD_DIR/raw_wordlist.txt"
METADATA_ZIP_FILE="$BUILD_DIR/raw_wordlist.txt"
DICT_FILE="$DIR/dictionary.hr_HR.txt"
LICENSE_FILE="$DIR/dictionary.hr_HR.LICENSE"

if [ -d "$BUILD_DIR" ]
then
    echo "Build directory $BUILD_DIR is not empty. Please remove first."
    exit 1
fi

mkdir -p "$BUILD_DIR"

echo "Fetching Croatian wordlist and converting from ISO_8859-2 to UTF8..."
curl "http://cvs.linux.hr/spell/wordlist/croatian-wordlist.txt.gz" | gunzip | iconv -f ISO_8859-2 -t UTF8 > "$RAW_WORDLIST_FILE"

echo "Lower-casing all words, and including only those that are 3 -> 9 characters long."
echo "Converting digraphs to utf8 as per https://github.com/lexica/lexica/issues/332#issuecomment-1279760597."
echo "Removing characters ä, q, w, x, and y."
cat "$RAW_WORDLIST_FILE" | \
	grep -P "^\p{Ll}*$" | \
	awk 'length($0) < 10 && length($0) > 2' | \
	sed 's/dž/ǆ/g' | \
	sed 's/lj/ǉ/g' | \
	sed 's/nj/ǌ/g' | \
	egrep -v "[äqwxy]" > ${DICT_FILE}

echo "Fetching metadata in order to obtain license..."
wget -O "$METADATA_ZIP_FILE" "http://cvs.linux.hr/spell/myspell/hr_HR.zip"

echo "Extracting license..."
unzip -p "$METADATA_ZIP_FILE" README_hr_HR.txt > "$LICENSE_FILE"

echo "Removing build directory '$BUILD_DIR'..."
rm -rf "$BUILD_DIR"
