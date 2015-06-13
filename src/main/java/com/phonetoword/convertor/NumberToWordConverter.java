package com.phonetoword.convertor;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Character.isDigit;

import com.google.common.base.CharMatcher;

import com.phonetoword.dictionary.Dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A service class to find word based equivalents of a set of phone numbers.
 */
public class NumberToWordConverter {

    private final Dictionary dictionary;

    /**
     * Initialise <code>NumberToWordConverter</code> instance with the dictionary to use.
     *
     * @param dictionary
     */
    public NumberToWordConverter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Map<String, List<String>> process(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            return process(br.lines());
        }
    }

    public Map<String, List<String>> process(Stream<String> lines) {
        final Map<String, List<String>> wordMap = new HashMap<>();
        lines.map(NumberToWordConverter::keepOnlyDigits).filter(number -> !isNullOrEmpty(number))
                .forEach(number -> wordMap.put(number, extractWords(number)));
        return wordMap;
    }

    private List<String> extractWords(String number) {
        final List<String> words = new ArrayList<>();
        getWordsForNumber("", number, words);
        return words;
    }

    private static String keepOnlyDigits(String word) {
        return CharMatcher.DIGIT.retainFrom(word);
    }

    private void getWordsForNumber(String processed, String unprocessed, List<String> words) {
        if (unprocessed.length() == 0) {
            words.add(processed);
            return;
        }

        matchAllPossibleWords(processed, unprocessed, words);

        considerSkippingOneDigit(processed, unprocessed, words);

        considerZeroAndOne(processed, unprocessed, words);

    }

    private void considerZeroAndOne(String processed, String unprocessed, List<String> words) {
        final char currentChar = unprocessed.charAt(0);
        // Zero and one never match a dictionary word
        if (currentChar == '0' || currentChar == '1') {
            getWordsForNumber(updateProcessed(processed, unprocessed.substring(0, 1)),
                    updateUnProcessed(unprocessed, unprocessed.substring(0, 1)), words);
        }
    }

    private void considerSkippingOneDigit(String processed, String unprocessed, List<String> words) {
        // Allow consideration of a run of up to <i>one<i> number to be
        // considered as an option
        if (processed.length() > 0 && !isDigit(processed.charAt(processed.length() - 1)) || processed.length() == 0) {
            getWordsForNumber(updateProcessed(processed, unprocessed.substring(0, 1)),
                    updateUnProcessed(unprocessed, unprocessed.substring(0, 1)), words);
        }
    }

    private void matchAllPossibleWords(String processed, String unprocessed, List<String> words) {
        // Check for words that start at the first number in the number part.
        for (int i = 1; i <= dictionary.getMaxWordLength() && i <= unprocessed.length(); i++) {
            final String number = unprocessed.substring(0, i);
            for (final String word : dictionary.getWordsForNumber(number)) {
                getWordsForNumber(updateProcessed(processed, word), updateUnProcessed(unprocessed, word), words);
            }
        }
    }

    private String updateProcessed(String processed, String current) {
        if (processed.length() == 0 || isDigit(processed.charAt(processed.length() - 1)) && isDigit(current.charAt(0))) {
            return processed + current;
        }
        return processed + "-" + current;
    }

    private String updateUnProcessed(String unprocessed, String current) {
        return unprocessed.substring(current.length());
    }

}
