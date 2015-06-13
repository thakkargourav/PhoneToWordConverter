package com.phonetoword.dictionary;

import com.google.common.collect.HashMultimap;

import java.util.Set;

/**
 * A collection of words, indexed by their numeric equivalent.
 */
public class Dictionary {

    private final HashMultimap<String, String> dictionary;
    private int maxWordLength = 0;
    private int totalWordCount = 0;

    public Dictionary() {
        dictionary = HashMultimap.create();
    }

    public int getMaxWordLength() {
        return maxWordLength;
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    /**
     * Lookup all words that match a given numeric sequence.
     *
     * @param number Number to lookup
     * @return Return set of words that match a given numeric sequence.
     */
    public Set<String> getWordsForNumber(String number) {
        return dictionary.get(number);
    }

    /**
     * Add a word to the dictionary, indexed by its numeric equivalent.
     *
     * @param number
     * @param word
     */
    public void addWord(String number, String word) {
        if (maxWordLength < word.length()) {
            maxWordLength = word.length();
        }
        dictionary.put(number, word);
        totalWordCount++;
    }

}
