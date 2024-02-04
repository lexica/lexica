package com.serwylo.lexica;


import com.serwylo.lexica.game.CharProbGenerator;
import com.serwylo.lexica.lang.Language;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public class LetterPointsTest {

    /**
     * Each language should have a static initializer block which contains a Scrabble-like score for
     * each letter. The compiler cannot enforce that we have all the correct letters, so this will
     * enumerate them and ask for a score for each language.
     */
    private final Language language;

    public LetterPointsTest(Language language) {
        super();
        this.language = language;
    }

    @Parameterized.Parameters(name = "Language: {index} {0}")
    public static List<Language[]> getAllLanguages() {
        List<Language[]> langs = new ArrayList<>(Language.getAllLanguages().size());
        for (Language lang : Language.getAllLanguages().values()) {
            langs.add(new Language[]{lang});
        }
        return langs;
    }

    /**
     * While it would be nicer to have this run in a non-instrumented unit test for performance,
     * we also want to be able to access resources that are in the compiled
     */
    @Test
    public void ensureLetterScoresExistForEntireAlphabet() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(language.getLetterDistributionFileName());
        Assert.assertNotNull("Letter probabilities exist for " + language.getName(), stream);

        CharProbGenerator charProbs = new CharProbGenerator(stream, language);

        for (String letter : charProbs.getAlphabet()) {
            language.getPointsForLetter(letter);
        }
    }
}
