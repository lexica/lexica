#!/bin/bash

LANGUAGE=$1

FOUND=`aspell dump dicts | grep ${LANGUAGE} | wc -l`

if [[ $FOUND == "0" ]]; then
	echo "Could not find language \"${LANGUAGE}\"."
	echo "Do you have the correct aspell dictionary installed?"
	echo "Check by running: aspell dump dicts"
	exit 1
fi

# The grep for lower case doesn't work for Farsi (and likely other languages) so I just excluded it for those languages.
#aspell -d ${LANGUAGE}  dump master | aspell -l ${LANGUAGE} expand | tr ' ' '\n' > assets/dictionaries/dictionary.${LANGUAGE}.txt

aspell -d ${LANGUAGE}  dump master | aspell -l ${LANGUAGE} expand | tr ' ' '\n' | grep -P "^\p{Ll}*$" | awk 'length($0) < 10 && length($0) > 2' > assets/dictionaries/dictionary.${LANGUAGE}.txt

echo "Wrote ./assets/dictionaries/dictionary.${LANGUAGE}.txt"
