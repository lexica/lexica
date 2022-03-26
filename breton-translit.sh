#! /bin/bash

# c'h and ch are considered as single letters in breton, so we map them to q and
# c, which are not used by themselves in breton language.
# There were some ', à and é in my dictionary, but they are not in the breton alphabet
# (in the peurunvan breton alphabet; see
# https://en.wikipedia.org/wiki/Breton_language#Alphabet )
sed "s/c'h/q/gi;s/ch/c/gi" | egrep -v "['àé]" 
