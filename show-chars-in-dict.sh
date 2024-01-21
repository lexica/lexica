#!/bin/bash
#
# Show all unique characters in a given dictionary.
# Useful in conjunction with ./remove-diacritics.sh in order to produce dictionaries that have
# normalized characters in them for easier and often more enjoyable play.
#

DICTIONARY="$1"

if [[ ! $DICTIONARY ]]; then
	echo "Must specify a dictionary name (e.g. 'pt', 'en_US' or 'fr_FR_no_diacritics')."
	echo "Usage: ./show-chars-in-dict.sh <dictionary name>"
	echo "Example: ./show-chars-in-dict.sh fr_FR_no_diacritics"
	exit 1
fi

DICT_FILE="assets/dictionaries/dictionary.${DICTIONARY}.txt"

if [[ ! -f "$DICT_FILE" ]]; then
    echo "Could not find dictionary for $DICTIONARY (looked in $DICT_FILE)"
    exit 1
fi 

# Adapted from https://stackoverflow.com/a/387704
grep -Pv '^(\s*|#.*)$' < "$DICT_FILE" | sed -e "s/./\0\n/g" | sort | uniq -c
echo ""

