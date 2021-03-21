#!/bin/bash
#
# Helper script to remove all diacritics from a given dictionary.
# Note this may not be suitable for all languages. All it does is convert it to an ASCII alternative.
# In French, this works well, but in German, a full normalization is required (e.g. ü is not just truncated
# to the ASCII "u", but rather the two ASCII characters "ue"). Hence, the German dictionary has its own
# specific script for manually replacing characters like this.
#


INPUT_DICT="$1"
OUTPUT_DICT="$2"

if [ ! $INPUT_DICT ] || [ ! $OUTPUT_DICT ]; then
	echo "Must specify an input and an output dictionary name (e.g. 'pt', 'en_US' or 'fr_FR_no_diacritics')."
	echo "Usage: ./remove-diacritics.sh <input dictionary name> <output dictionary name>"
	echo "Example: ./remove-diacritics.sh fr_FR fr_FR_no_diacritics"
	exit 1
fi

INPUT_DICT_FILE="assets/dictionaries/dictionary.$INPUT_DICT.txt"
OUTPUT_DICT_FILE="assets/dictionaries/dictionary.$OUTPUT_DICT.txt"

if [[ ! -f "$INPUT_DICT_FILE" ]]; then
    echo "Could not find dictionary for $INPUT_DICT (looked in $INPUT_DICT_FILE)"
    exit 1
fi 

# https://stackoverflow.com/a/10207623
iconv -f utf8 -t ascii//TRANSLIT "$INPUT_DICT_FILE" | sort | uniq > "$OUTPUT_DICT_FILE"
