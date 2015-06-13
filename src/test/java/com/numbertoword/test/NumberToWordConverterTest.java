package com.numbertoword.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.phonetoword.convertor.NumberToWordConverter;
import com.phonetoword.dictionary.Dictionary;
import com.phonetoword.dictionary.reader.DictionaryReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test phone number converter.
 */
public class NumberToWordConverterTest {

    private static Dictionary dictionary;
    private static NumberToWordConverter numberToWordConverter;
    private static Path testData;

    @BeforeClass
    public static void init() throws IOException {
        dictionary = DictionaryReader.load(Paths.get("src/test/resources/test-dictionary.txt"));
        assertEquals(9, dictionary.getTotalWordCount());
        numberToWordConverter = new NumberToWordConverter(dictionary);
        testData = Paths.get("src/test/resources/test-data.txt");
    }

    @Test
    public void testDoesnotReturnNull() throws IOException {
        assertNotNull(numberToWordConverter.process(testData));
    }

    @Test
    public void testPunctuaitonExclusion() throws IOException {
        final Map<String, List<String>> extractWords =
                numberToWordConverter.process(Arrays.asList("(2)255.63", "22,556#3").stream());
        final List<String> expectedWords = Arrays.asList("CALL-ME");
        assertThat(extractWords).containsOnlyKeys("225563").containsValue(expectedWords)
                .containsOnly(entry("225563", expectedWords));
    }

    @Test
    public void testAllowSingleDigit() throws IOException {
        // Allows single digit at start of number
        Map<String, List<String>> extractWords = numberToWordConverter.process(Arrays.asList("4(2)255.63").stream());

        assertThat(extractWords).containsOnlyKeys("4225563");
        assertThat(extractWords.get("4225563")).contains("4-CALL-ME");

        // Allows single digit in middle of number
        extractWords = numberToWordConverter.process(Arrays.asList("(2)2554.63").stream());

        assertThat(extractWords).containsOnlyKeys("2255463");
        assertThat(extractWords.get("2255463")).contains("CALL-4-ME");

        // Allows single digit at end of number
        extractWords = numberToWordConverter.process(Arrays.asList("(2)255.634").stream());

        assertThat(extractWords).containsOnlyKeys("2255634");
        assertThat(extractWords.get("2255634")).contains("CALL-ME-4");
    }

    @Test
    public void testDisallowingMoreOneDigit() throws IOException {
        final Map<String, List<String>> extractWords = numberToWordConverter.process(Arrays.asList("44(2)255.63").stream());

        assertThat(extractWords).containsOnlyKeys("44225563");
        assertThat(extractWords.get("44225563")).hasSize(0);

    }

}
