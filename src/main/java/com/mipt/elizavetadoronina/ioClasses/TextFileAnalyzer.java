package com.mipt.elizavetadoronina.ioClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final Map<Character, Long> charFrequency;

    public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Long> charFrequency, long lineCount1, long wordCount1, long charCount1, Map<Character, Long> charFrequency1) {
      this.lineCount = lineCount1;
      this.wordCount = wordCount1;
      this.charCount = charCount1;
      this.charFrequency = charFrequency1;
    }

    public long getLineCount() {
      return lineCount;
    }

    public long getWordCount() {
      return wordCount;
    }

    public long getCharCount() {
      return charCount;
    }

    public Map<Character, Long> getCharFrequency() {
      return charFrequency;
    }
  }

  public AnalysisResult analyzeFile(String filePath) throws IOException {
    long lineCount = 0;
    long wordCount = 0;
    long charCount = 0;
    Map<Character, Long> charFrequency = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lineCount++;

        String[] words = line.trim().split()
      }
    }
  }
}