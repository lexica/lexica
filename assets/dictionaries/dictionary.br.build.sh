#! /bin/bash

# c'h and ch are considered as single letters in breton, so we map them to q and
# c, which are not used by themselves in breton language.
# There were some ', à and é in my dictionary, but they are not in the breton alphabet
# (in the peurunvan breton alphabet; see
# https://en.wikipedia.org/wiki/Breton_language#Alphabet )

DIR=$(dirname "$0")

ACCENTED_DICT="$DIR/dictionary.br.txt"
UNACCENTED_DICT="$DIR/dictionary.br_no_diacritics.txt"

RAW_DICT="$ACCENTED_DICT"

TEMP_DICT_FILE="$DIR/temporary_dictionary.br.txt"

# Do as in add-lang.sh, without grep for lower case to keep the "c'h"s
aspell -l br dump master | aspell -l br expand | tr ' ' '\n' | awk 'length($0) < 10 && length($0) > 2' | sort -u > $TEMP_DICT_FILE

sed "s/c'h/q/gi;s/ch/c/gi" "$RAW_DICT" | egrep -v "[-'àé]" | sort -u > "$TEMP_DICT_FILE"

mv "$TEMP_DICT_FILE" "$ACCENTED_DICT"

iconv -f utf8 -t ascii//TRANSLIT "$ACCENTED_DICT" | sort -u > "$UNACCENTED_DICT"

echo "Accented dictionary: $ACCENTED_DICT"
echo "Unaccented dictionary: $UNACCENTED_DICT"
