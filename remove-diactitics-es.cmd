where $path:sed.exe
if errorlevel 1 goto :error
where $path:uniq.exe
if errorlevel 1 goto :error
chcp 65001
sed -f spanish-translit-utf8.sed assets\dictionaries\dictionary.es.txt >assets\dictionaries\dictionary.es-enne.tmp
sort assets\dictionaries\dictionary.es-enne.tmp /O assets\dictionaries\dictionary.es-enne-2.tmp
uniq assets\dictionaries\dictionary.es-enne-2.tmp assets\dictionaries\dictionary.es_solo_enne.txt

REM the KEEP parameter allows to keep the files previous to SORT and UNIQ,
REM just in case SORT and UNIQ are not respecting the UTF-8 charset.
if /I %1.==keep. goto :fin
del assets\dictionaries\dictionary.es-enne.tmp
del assets\dictionaries\dictionary.es-enne-2.tmp


goto :fin
:error
echo This script requires sed.exe and uniq.exe
echo.
echo It can be obtained from many unixline-windows suites, 
echo such as migw or statbins.
echo.
echo Alternatively, install the linux extensions of windows 10 and
echo run remove-diacritics-es.sh
echo.
:fin
