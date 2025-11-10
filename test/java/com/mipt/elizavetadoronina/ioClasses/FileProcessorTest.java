package com.mipt.elizavetadoronina.ioClasses;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorTest {

  @TempDir
  Path tempDir;

  @Test
  void testSplitAndMergeFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = Files.createTempFile("test", ".dat");
    byte[] testData = new byte[1500];
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    String outputDir = Files.createTempDirectory("parts").toString();
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

    assertEquals(3, parts.size(), "Should create 3 parts for 1500 bytes with 500 byte part size");

    assertEquals(500, Files.size(parts.get(0)), "First part should be 500 bytes");
    assertEquals(500, Files.size(parts.get(1)), "Second part should be 500 bytes");
    assertEquals(500, Files.size(parts.get(2)), "Third part should be 500 bytes");

    assertTrue(parts.get(0).toString().endsWith("test.dat.part1"));
    assertTrue(parts.get(1).toString().endsWith("test.dat.part2"));
    assertTrue(parts.get(2).toString().endsWith("test.dat.part3"));

    Path mergedFile = tempDir.resolve("merged.dat");
    processor.mergeFiles(parts, mergedFile.toString());

    assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
  }

  @Test
  void testSplitFileWithExactDivision() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = tempDir.resolve("exact.dat");
    byte[] testData = new byte[1000]; // 1KB данных
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    Path outputDir = tempDir.resolve("exact_parts");
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 250);

    assertEquals(4, parts.size(), "Should create 4 parts for 1000 bytes with 250 byte part size");

    for (Path part : parts) {
      assertEquals(250, Files.size(part), "Each part should be exactly 250 bytes");
    }
  }

  @Test
  void testSplitFileWithRemainder() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = tempDir.resolve("remainder.dat");
    byte[] testData = new byte[1200];
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    Path outputDir = tempDir.resolve("remainder_parts");
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 500);

    assertEquals(3, parts.size(), "Should create 3 parts for 1200 bytes with 500 byte part size");

    assertEquals(500, Files.size(parts.get(0)), "First part should be 500 bytes");
    assertEquals(500, Files.size(parts.get(1)), "Second part should be 500 bytes");
    assertEquals(200, Files.size(parts.get(2)), "Third part should be 200 bytes (remainder)");
  }

  @Test
  void testSplitEmptyFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = tempDir.resolve("empty.dat");
    Files.write(testFile, new byte[0]);

    Path outputDir = tempDir.resolve("empty_parts");
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 500);

    assertEquals(1, parts.size(), "Should create 1 part for empty file");
    assertTrue(Files.exists(parts.get(0)));
    assertEquals(0, Files.size(parts.get(0)), "The part should be empty");
  }

  @Test
  void testMergeWithNonExistentPart() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path outputFile = tempDir.resolve("output.dat");
    List<Path> nonExistentParts = Arrays.asList(
            tempDir.resolve("nonexistent1.part1"),
            tempDir.resolve("nonexistent2.part2")
    );

    assertThrows(IOException.class, () -> {
      processor.mergeFiles(nonExistentParts, outputFile.toString());
    });
  }

  @Test
  void testMergeSingleFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path singleFile = tempDir.resolve("single.dat");
    byte[] testData = new byte[100];
    new Random().nextBytes(testData);
    Files.write(singleFile, testData);

    Path outputFile = tempDir.resolve("merged_single.dat");
    processor.mergeFiles(Arrays.asList(singleFile), outputFile.toString());

    assertArrayEquals(Files.readAllBytes(singleFile), Files.readAllBytes(outputFile));
  }
}