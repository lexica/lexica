package com.serwylo.lexica.lang

import org.junit.Test

import org.junit.Assert.*

class LanguageTest {

    /**
     * Part of adding a new language means putting it in the [Language.allLanguages] map.
     * This map requires a key to be present which matches the name of the language, as
     * it will be used for lookups later. This ensures it is done correctly.
     */
    @Test
    fun ensureLanguagesAreCorrectlyIndexed() {

        Language.allLanguages.forEach {
            val code = it.key
            val lang = it.value

            assertEquals(code, lang.name)
        }

    }

}