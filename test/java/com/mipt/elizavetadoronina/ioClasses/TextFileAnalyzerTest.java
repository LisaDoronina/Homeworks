package com.mipt.elizavetadoronina.ioClasses;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TextFileAnalyzerTest {

  @Test
  void testAnalyzerFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path testFile = Files.createTempFile("text", ".txt");
    Files.write(testFile, Arrays.asList("Hello world!", "This is test."));

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

    assertEquals(2, result.getLineCount());
    assertEquals(5, result.getWordCount());
    assertEquals(27, result.getCharCount());

    Map<Character, Long> frequency = result.getCharFrequency();
    assertEquals(3L, frequency.get('l'));
    assertEquals(3L, frequency.get(' '));
    assertEquals(3L, frequency.get('s'));
    assertEquals(2L, frequency.get('e'));

    Files.delete(testFile);
  }

  @Test
  void testAnalyzeEmpty() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path testFile = Files.createTempFile("empty", ".txt");
    Files.write(testFile, Arrays.asList(""));


    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

    assertEquals(1, result.getLineCount());
    assertEquals(0, result.getWordCount());
    assertEquals(0, result.getCharCount());
    assertTrue(result.getCharFrequency().isEmpty());

    Files.delete(testFile);
  }

  @Test
  void testSaveAnalysisResult() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Map<Character, Long> frequency = new HashMap<>();
    frequency.put('H', 1L);
    frequency.put('e', 1L);
    frequency.put('l', 2L);
    frequency.put('o', 1L);
    frequency.put(' ', 1L);
    frequency.put('!', 1L);

    TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(1, 2, 7, frequency);

    Path outputFile = Files.createTempFile("analysis", ".txt");
    analyzer.saveAnalysisResult(result, outputFile.toString());

    assertTrue(Files.size(outputFile) > 0);

    String content = Files.readString(outputFile);
    assertTrue(content.contains("Line count: 1"));
    assertTrue(content.contains("Word count: 2"));
    assertTrue(content.contains("Character count: 7"));
    assertTrue(content.contains("'H': 1"));
    assertTrue(content.contains("'l': 2"));
    assertTrue(content.contains("'space': 1"));

    Files.delete(outputFile);
  }
}
