#!/bin/bash

DIR=$(dirname "$0")

DICT_FILE_DIACRITICS="$DIR/dictionary.de_DE.txt"
LICENSE_FILE_DIACRITICS="$DIR/dictionary.de_DE.LICENSE"

DICT_FILE="$DIR/dictionary.de_DE_no_diacritics.txt"
LICENSE_FILE="$DIR/dictionary.de_DE_no_diacritics.LICENSE"

if [ ! -f "$DICT_FILE_DIACRITICS" ]
then
    echo "Source dictionary $DICT_FILE_DIACRITICS does not exist. Perhaps you need to run dictionary.de_DE.build.sh first?"
    exit 1
fi

echo "Copying source dictionary containing diacritics from $DICT_FILE_DIACRITICS..."
cp "$DICT_FILE_DIACRITICS" "$DICT_FILE"
cp "$LICENSE_FILE_DIACRITICS" "$LICENSE_FILE"

echo "Replacing ä/Ä->ae, ö/Ö->oe, ü/Ü->ue..."
sed -i "s/ä/ae/g" "$DICT_FILE"
sed -i "s/Ä/ae/g" "$DICT_FILE"
sed -i "s/ö/oe/g" "$DICT_FILE"
sed -i "s/Ö/oe/g" "$DICT_FILE"
sed -i "s/ü/ue/g" "$DICT_FILE"
sed -i "s/Ü/ue/g" "$DICT_FILE"

echo "Removing duplicate words introduced after transforming characters with diacritics."
cat "$DICT_FILE" | sort | uniq > "$DICT_FILE.tmp" && mv "$DICT_FILE.tmp" "$DICT_FILE" || rm -f "$DICT_FILE.tmp"
