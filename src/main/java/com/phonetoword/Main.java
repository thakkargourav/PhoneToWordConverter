package com.phonetoword;

import com.google.common.base.Joiner;

import com.phonetoword.convertor.NumberToWordConverter;
import com.phonetoword.dictionary.Dictionary;
import com.phonetoword.dictionary.reader.DictionaryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main entry point of this utility. Validates the input parameters, and passes them on to the
 * <code>NumberToWordConverter</code> class for analysis.
 */
public class Main {

    private static final String EXIT_COMMAND = "exit";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String DEFAULT_DICTIONARY_PATH = "data/default_dictionary.txt";
    private static final Joiner JOINER = Joiner.on("\n").skipNulls();

    public static void main(String[] args) throws IOException {

        List<Path> dataFileList = null;
        final Dictionary dictionary;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-d")) {
                dictionary = readDictionaryFromPath(args);
                if (args.length > 2) {
                    dataFileList = resolveFiles(args, 2);
                }
            } else {
                dictionary = readDefaultDictionary();
                dataFileList = resolveFiles(args, 0);
            }
        } else {
            dictionary = readDefaultDictionary();
        }

        if (dictionary.getTotalWordCount() == 0) {
            LOGGER.error("The dictionary file {} contains no words that can be mapped to phone number.");
            System.exit(1);
        }

        final NumberToWordConverter numberToWordConverter = new NumberToWordConverter(dictionary);

        if (dataFileList != null) {
            fileParser(dataFileList, numberToWordConverter);
        } else {
            cmdParser(numberToWordConverter);
        }
    }

    private static Dictionary readDictionaryFromPath(String[] args) throws IOException {
        final Dictionary dictionary;
        final Path dictionaryPath = Paths.get(args[1]);
        LOGGER.info("Dictionary {} passed as arguement. Overriding default Dictionary.", dictionaryPath);
        if (!Files.isReadable(dictionaryPath)) {
            LOGGER.error("Dictionary file: {} is missing or not readable.", dictionaryPath);
            System.exit(1);
        }
        dictionary = DictionaryReader.load(dictionaryPath);
        return dictionary;
    }

    private static Dictionary readDefaultDictionary() throws IOException {
        return DictionaryReader.load(Main.class.getClassLoader().getResourceAsStream(DEFAULT_DICTIONARY_PATH));
    }

    private static void fileParser(final List<Path> dataFileList, final NumberToWordConverter numberToWordConverter) {
        dataFileList.forEach(df -> {
            if (Files.isReadable(df)) {
                try {
                    printGeneratedWords(numberToWordConverter.process(df));
                } catch (final Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } else {
                LOGGER.error("Data file: {} is missing or not readable.", df);
            }
        });
    }

    private static void cmdParser(final NumberToWordConverter numberToWordConverter) {
        LOGGER.info("No data file specified. Taking input from STDIN.");
        try (Scanner in = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            final List<String> numbers = new ArrayList<>();
            String line;
            LOGGER.info("Please enter one phone number per line. Type \"{}\" if you want to end:", EXIT_COMMAND);
            while (true) {
                line = in.nextLine().trim();
                if (line.contains(EXIT_COMMAND)) {
                    LOGGER.info("Completed taking all numbers as input. Please find the result below:");
                    break;
                } else {
                    numbers.add(line);
                }
            }
            printGeneratedWords(numberToWordConverter.process(numbers.stream()));
        }
    }

    private static void printGeneratedWords(Map<String, List<String>> wordMap) {
        wordMap.entrySet().forEach(
                es -> LOGGER.info("List of possible words for number {} are:\n{}", es.getKey(),
                        JOINER.join(es.getValue())));
    }

    private static List<Path> resolveFiles(String[] args, int index) {
        return Arrays.stream(Arrays.copyOfRange(args, index, args.length)).map(Paths::get).collect(Collectors.toList());
    }

}
