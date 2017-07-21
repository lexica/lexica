package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.CharProbGenerator;
import com.serwylo.lexica.lang.DeGerman;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.UkEnglish;
import com.serwylo.lexica.lang.UsEnglish;
import com.serwylo.lexica.trie.util.LetterFrequency;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GA {

    private static final int NUM_OF_FITTEST_TO_COPY = 1;

    /**
     * In case a run gets stuck in a local optimum that it can't break out of, start an entire
     * fresh run with new random genomes this many times.
     */
    private static final int SEPARATE_RUNS = 5;
    private static final int ITERATIONS = 1000;
    private static final int NUM_OF_GENOMES = 20;
    private static final int BOARDS_TO_GENERATE_FOR_FITNESS_CALC = 100;
    private static final double RATE_OF_MUTATION = 0.05;
    private static final double RATE_OF_NEW_RANDOM_GENOMES = 0.05;

    @Test
    public void run() throws IOException, InterruptedException {
        final Language language = new DeGerman();

        for (int i = 0; i < SEPARATE_RUNS; i ++) {
            Genome best = generateProbabilityDistribution(language);

            System.out.println("[" + language.getName() + ", run " + (i + 1) + "]");
            System.out.println(best.toString());
            System.out.println("Random board:");
            System.out.println(renderBoardToString(best.toCharProbGenerator().generateFourByFourBoard()));

            String fileName = "/home/pete/code/lexica/assets/candidate-boards/" +
                    language.getName() + " " +
                    "Min: " + (int) best.getFitness().stats.getMin() + " " +
                    "Mean: " + (int) best.getFitness().stats.getMean() + " " +
                    "Max: " + (int) best.getFitness().stats.getMax() + " " +
                    "Score: " + (int) best.getFitness().getScore();

            File output = new File(fileName);
            FileWriter writer = new FileWriter(output);
            writer.write(best.getFitness().toString() + "\n" + best.toString());
            writer.close();
        }
    }

    private static String renderBoardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < board.getWidth(); y ++) {
            for (int x = 0; x < board.getWidth(); x ++) {
                String value = board.valueAt(x + y * board.getWidth());
                sb.append(value).append("\t");
            }
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    private Genome generateProbabilityDistribution(Language language) throws IOException, InterruptedException {

        List<Genome> currentPopulation = new ArrayList<>();
        for (int genomeNum = 0; genomeNum < NUM_OF_GENOMES; genomeNum++) {
            currentPopulation.add(Genome.createRandom(language));
        }

        sortInPlaceByFitness(currentPopulation);

        for (int iteration = 0; iteration < ITERATIONS; iteration ++) {

            List<Genome> nextPopulation = new ArrayList<>(currentPopulation.size());

            SummaryStatistics stats = summariseGenomeScores(currentPopulation);
            for (Genome genome : currentPopulation) {
                // Add the best scoring genome straight into the new population
                if ((int) genome.getFitness().getScore() == (int) stats.getMax()) {
                    nextPopulation.add(genome);
                }
            }

            while (nextPopulation.size() < currentPopulation.size()) {
                // 10% of the time throw in a new random genome to refresh the population.
                if (Math.random() < 0.1) {
                    nextPopulation.add(Genome.createRandom(language));
                } else {
                    Genome mother = selectByFitness(currentPopulation);
                    Genome father = selectByFitness(currentPopulation);
                    nextPopulation.add(mother.breedWith(father));
                }
            }

            sortInPlaceByFitness(nextPopulation);

            Fitness best = nextPopulation.get(nextPopulation.size() - 1).getFitness();

            System.out.println("Iteration: " + (iteration + 1) + " (" + (int) best.getScore() + ") [" + best + "]");

            currentPopulation = nextPopulation;
        }

        return currentPopulation.get(currentPopulation.size() - 1);
    }

    private static class CalcFitness implements Callable<Fitness> {

        private final Genome genome;

        private CalcFitness(Genome genome) {
            this.genome = genome;
        }

        @Override
        public Fitness call() throws Exception {
            return genome.getFitness();
        }
    }

    private static SummaryStatistics summariseGenomeScores(List<Genome> genomes) throws IOException {
        SummaryStatistics stats = new SummaryStatistics();
        for (Genome genome : genomes) {
            stats.addValue(genome.getFitness().getScore());
        }
        return stats;
    }

    private static Genome selectByFitness(List<Genome> genomes) throws IOException {
        double total = 0;
        for (Genome genome : genomes) {
            total += genome.getFitness().getScore();
        }

        double selection = Math.random() * total;

        double tally = 0;
        for (Genome genome: genomes) {
            tally += genome.getFitness().getScore();
            if (tally > selection) {
                return genome;
            }
        }

        throw new IllegalStateException("Should have chosen one of the genomes, but didn't.");
    }

    private static void sortInPlaceByFitness(List<Genome> genomes) throws InterruptedException {

        Collection<Callable<Fitness>> tasks = new ArrayList<>(genomes.size());
        for (Genome genome : genomes) {
            tasks.add(new CalcFitness(genome));
        }
        ExecutorService executor = Executors.newFixedThreadPool(20);
        executor.invokeAll(tasks);
        executor.shutdown();

        Collections.sort(genomes, new Comparator<Genome>() {
            @Override
            public int compare(Genome lhs, Genome rhs) {
                try {
                    return (int) (lhs.getFitness().getScore() - rhs.getFitness().getScore());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    static class Fitness {

        private final SummaryStatistics stats;

        public static Fitness calc(Genome genome, Language language) throws IOException {
            return calc(genome, language, BOARDS_TO_GENERATE_FOR_FITNESS_CALC);
        }

        public static Fitness calc(Genome genome, Language language, int work) throws IOException {
            return new Fitness(generateStats(genome, language, work));
        }

        private static Map<Language, byte[]> cachedTries = new HashMap<>();

        private static InputStream trieReader(Language language) throws IOException {
            if (!cachedTries.containsKey(language)) {
                byte[] buffer = new byte[1024 * 1024 * 2]; // 2MiB
                InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getTrieFileName());
                int read = stream.read(buffer);
                byte[] total = new byte[read];
                System.arraycopy(buffer, 0, total, 0, read);
                cachedTries.put(language, total);
            }

            return new ByteArrayInputStream(cachedTries.get(language));
        }

        private static SummaryStatistics generateStats(Genome genome, Language language, int iterations) throws IOException {
            SummaryStatistics stats = new SummaryStatistics();
            for (int i = 0; i < iterations; i ++) {
                Board board = genome.toCharProbGenerator().generateFourByFourBoard();
                try {
                    InputStream stream = trieReader(language);
                    Trie dict = new StringTrie.Deserializer().deserialize(stream, board, language);
                    int numWords = dict.solver(board, new WordFilter.MinLength(3)).size();
                    stats.addValue(numWords);
                } catch(IOException ignored) { }
            }
            return stats;
        }

        Fitness(SummaryStatistics stats) {
            this.stats = stats;
        }

        double getScore() throws IOException {

            return stats.getMin() * 10 + stats.getMean() * 10 + stats.getMax() / 2;

            /*
            // Heavily penalise boards which result in a board of 0 words.
            if (stats.getMin() == 0) {
                return stats.getMax() / 10;
            }

            // Slightly penalise those which result in boards of less than 10.
            if (stats.getMean() < 10) {
                return stats.getMax() / 2;
            }

            return stats.getMax();
            */
        }

        private String cachedStringRepresentation = null;

        public String toString() {
            if (cachedStringRepresentation == null) {
                try {
                    cachedStringRepresentation = "Min: " + (int) stats.getMin() + ", mean: " + (int) stats.getMean() + ", max: " + (int) stats.getMax() + ", score: " + (int) getScore();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return cachedStringRepresentation;
        }
    }

    static class Gene {

        public static Gene createRandom(String letter, int count) {
            int currentCount = (int) (Math.random() * 100);
            List<Integer> occurrences = new ArrayList<>();

            for (int i = 1; i <= count; i ++) {
                occurrences.add(currentCount);
                int max = Math.max(0, currentCount - (i * 10));
                currentCount = Math.max(1, (int) (Math.random() * max));
            }

            return new Gene(letter, occurrences);
        }

        final String letter;
        final List<Integer> occurrences;

        public Gene(String letter, List<Integer> occurrences) {
            this.letter = letter;
            this.occurrences = occurrences;
        }

    }

    static class Genome {

        static Genome createRandom(Language language) throws IOException {
            LetterFrequency letters = allLetters(language);
            List<Gene> genes = new ArrayList<>(letters.getLetters().size());
            for (String letter : letters.getLetters()) {
                genes.add(Gene.createRandom(letter, letters.getCountsForLetter(letter).size()));
            }

            return new Genome(language, genes);
        }

        static Map<Language, LetterFrequency> letterFrequencies = new HashMap<>();

        private static LetterFrequency allLetters(Language language) throws IOException {
            LetterFrequency cachedFrequencies = letterFrequencies.get(language);
            if (cachedFrequencies != null) {
                return cachedFrequencies;
            }

            InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getDictionaryFileName());
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            LetterFrequency letters = new LetterFrequency(language);

            String line;
            while((line = br.readLine()) != null) {
                String word = line.toLowerCase(language.getLocale());
                letters.addWord(word);
            }

            letterFrequencies.put(language, letters);
            return letters;
        }

        final Language language;
        final List<Gene> genes;
        private String cachedStringRepresentation = null;

        Genome(Language language, List<Gene> genes) {
            this.language = language;
            this.genes = genes;
        }

        public String toString() {
            if (cachedStringRepresentation == null) {
                StringBuilder sb = new StringBuilder(500);
                boolean first = true;
                for (Gene gene : genes) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append('\n');
                    }

                    sb.append(gene.letter);
                    for (Integer occurrence : gene.occurrences) {
                        sb.append(' ');
                        sb.append(occurrence);
                    }
                }
                cachedStringRepresentation = sb.toString();
            }

            return cachedStringRepresentation;
        }

        private CharProbGenerator toCharProbGenerator() {
            return new CharProbGenerator(new ByteArrayInputStream(toString().getBytes()));
        }

        private Fitness cachedFitness = null;

        Fitness getFitness() throws IOException {
            if (cachedFitness == null) {
                cachedFitness = Fitness.calc(this, language);
            }

            return cachedFitness;
        }

        public Genome breedWith(Genome mate) throws IOException {
            List<Gene> child = new ArrayList<>(genes.size());
            for (int i = 0; i < genes.size(); i ++) {
                double random = Math.random();
                if (random < 0.05) {
                    child.add(Gene.createRandom(genes.get(i).letter, allLetters(language).getCountsForLetter(genes.get(i).letter).size()));
                } else if (random < 0.5) {
                    child.add(genes.get(i));
                } else {
                    child.add(mate.genes.get(i));
                }
            }
            return new Genome(language, child);
        }
    }
}
