#!/usr/bin/python3
#
# Identify translations that are above a certain completness from weblate.
#
# Adapted from the GPLv3+ fdroidclient project.
#

import os
import re
import requests

threshold = 70

url = 'https://hosted.weblate.org/exports/stats/lexica/strings/?format=json'
response = requests.get(url)
response.raise_for_status()

locales = dict()
for locale in response.json():
    locales[locale['code']] = locale

for locale in sorted(locales.keys(), reverse=True):
    metadata = locales.get(locale)
    if metadata is not None and metadata['translated_percent'] > threshold:#  and a['failing'] == 0:
        print(locale)
