package com.numbertoword.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.phonetoword.dictionary.Dictionary;
import com.phonetoword.dictionary.reader.DictionaryReader;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Tests that exercise the boundary conditions of each of the internal dictionary functions.
 */
public class DictionaryTest {

    @Test(expected = IOException.class)
    public void testThrowsIOExceptionIfFileNotFound() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-notexist.txt"));
        dictionary.getTotalWordCount();
        fail();
    }

    @Test
    public void testCannotInstantiateDictionaryReader() throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Constructor<DictionaryReader> constructor = DictionaryReader.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testOnlyLoadsAlphabeticalWords() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());
    }

    @Test
    public void testMaxWordLength() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(7, dictionary.getMaxWordLength());
    }

    @Test
    public void testDoesNotLoadDuplicateWords() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());
    }

    @Test
    public void testIgnoresPunctuation() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());

        final Set<String> set = dictionary.getWordsForNumber("63");
        assertThat(set).containsOnly("ME");
    }

    @Test
    public void testStoresInUppercase() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());

        final Set<String> set = dictionary.getWordsForNumber("2667883");
        assertThat(set).containsOnly("COMPUTE");

    }

    @Test
    public void testReturnsCorrectMatches() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());

        final Set<String> set = dictionary.getWordsForNumber("2");
        assertThat(set).containsOnly("A", "B");
    }

    @Test
    public void testDoesNotReturnNull() throws IOException {
        final Dictionary dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertNotNull(dictionary.getWordsForNumber("2892287287628376476484272636734646565727223423223444422"));
    }

}
