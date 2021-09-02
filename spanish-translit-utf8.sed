#Standard transliteration used in spanish versions of Scrabble and Boggle.
#Ntilde (Ñ) is respected, Accents are not.
#This scheme is also respected in most Spanish crossword-like puzzles
#I'm using SED instead of TR because SED will take the chars as they are, regardless of codepage.
s/Á/a/g
s/á/a/g
s/É/e/g
s/é/e/g
s/Í/i/g
s/í/i/g
s/Ó/o/g
s/ó/o/g
s/Ú/u/g
s/ú/u/g
#I think intelect (a spanish version of scrabble published by CEFA) respected U umlaut
#Uumlaut can appear twice in a word, while rest of accented letters cannot.
#But I will delete it regardless of it.
s/Ü/u/g
s/ü/u/g

