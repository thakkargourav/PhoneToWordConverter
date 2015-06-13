package com.phonetoword.dictionary.reader;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.phonetoword.dictionary.Dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Static class to load all relevant words from a dictionary that are valid for use in a phone number. Store in an
 * in-memory ordered structure that is for efficient lookup.
 */
public class DictionaryReader {

    /**
     * private constructor to avoid instantiation
     */
    private DictionaryReader() {}

    /**
     * Load each usable word from a dictionary file into a dictionary.
     *
     * @param path path for the file from which words need to be loaded in dictionary
     * @return Dictionary an in-memory ordered structure to store number and word map
     * @throws IOException
     */
    public static Dictionary load(Path path) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            return load(bufferedReader.lines());
        }
    }

    /**
     * Load each usable word from a dictionary file into a dictionary.
     *
     * @param inputStream InputStream for the file from which words need to be loaded in dictionary
     * @return Dictionary an in-memory ordered structure to store number and word map
     * @throws IOException
     */
    public static Dictionary load(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return load(bufferedReader.lines());
        }
    }

    /**
     * Load each usable word from a dictionary file into a dictionary.
     *
     * @param lines stream of lines which need to be loaded in dictionary
     * @return Dictionary an in-memory ordered structure to store number and word map
     */
    public static Dictionary load(Stream<String> lines) {
        final Dictionary dictionary = new Dictionary();
        lines.map(DictionaryReader::keepOnlyAlphabets).distinct().filter(word -> !isNullOrEmpty(word))
                .map(String::toUpperCase).forEach(word -> {
                    final String number = wordToNumber(word);
                    dictionary.addWord(number, word);
                });
        return dictionary;
    }

    /**
     * Returns the phone dialing sequence corresponding to a word.
     *
     * @param word
     * @return the phone dialing sequence corresponding to a word.
     */
    private static String wordToNumber(String word) {
        final StringBuilder number = new StringBuilder(word.length());
        for (int i = 0; i < word.length(); i++) {
            number.append(getNumber(word.charAt(i)));
        }
        return number.toString();
    }

    private static String keepOnlyAlphabets(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    private static char getNumber(int uppercaseLetter) {
        switch (uppercaseLetter) {
            case 'A':
            case 'B':
            case 'C':
                return '2';
            case 'D':
            case 'E':
            case 'F':
                return '3';
            case 'G':
            case 'H':
            case 'I':
                return '4';
            case 'J':
            case 'K':
            case 'L':
                return '5';
            case 'M':
            case 'N':
            case 'O':
                return '6';
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
                return '7';
            case 'T':
            case 'U':
            case 'V':
                return '8';
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                return '9';
            default:
                return '0';
        }
    }
}
