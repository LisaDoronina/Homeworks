package com.mipt.elizavetadoronina.ioClasses;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final Map<Character, Long> charFrequency;

    public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Long> charFrequency) {
      this.lineCount = lineCount;
      this.wordCount = wordCount;
      this.charCount = charCount;
      this.charFrequency = charFrequency;
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

        String trimmedLine = line.trim();
        if (!trimmedLine.isEmpty()) {
          String[] words = trimmedLine.split("\\s+");
          wordCount += words.length;
        }

        for (char c : line.toCharArray()) {
          charCount++;
          charFrequency.put(c, charFrequency.getOrDefault(c, 0L) + 1);
        }

        if (reader.ready()) {
          charCount++;
          charFrequency.put('\n', charFrequency.getOrDefault('\n', 0L) + 1);
        }
      }

      if (charCount > 0) {
        charCount--;
        charFrequency.put('\n', charFrequency.get('\n') - 1);
        if (charFrequency.get('\n') == 0) {
          charFrequency.remove('\n');
        }
      }
    }
    return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
  }

  public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      writer.write("Line count: " + result.getLineCount());
      writer.newLine();
      writer.write("Word count: " + result.getWordCount());
      writer.newLine();
      writer.write("Character count: " + result.getCharCount());
      writer.newLine();
      writer.write("Character Frequency:");
      writer.newLine();

      for (Map.Entry<Character, Long> entry : result.getCharFrequency().entrySet()) {
        char character = entry.getKey();
        String charRepresentation = getCharRepresentation(character);
        writer.write("'" + charRepresentation + "': " + entry.getValue());
        writer.newLine();
      }
    }
  }

  private String getCharRepresentation(char c) {
    switch (c) {
      case '\n': return "\\n";
      case '\r': return "\\r";
      case '\t': return "\\t";
      case ' ': return "space";
      default: return String.valueOf(c);
    }
  }
}