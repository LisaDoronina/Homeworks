package com.mipt.elizavetadoronina.ioClasses;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

  public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {

    Path sourceFile = Paths.get(sourcePath);
    Path outputDirectory = Paths.get(outputDir);

    Files.createDirectories(outputDirectory);

    List<Path> partFiles = new ArrayList<>();
    String originalFileName = sourceFile.getFileName().toString();

    try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      long fileSize = sourceChannel.size();
      long bytesRead = 0;
      int partNumber = 1;

      ByteBuffer buffer = ByteBuffer.allocate(partSize);

      while (bytesRead < fileSize) {
        String partFileName = String.format("%s.part%d", originalFileName, partNumber);
        Path partFile = outputDirectory.resolve(partFileName);

        try(FileChannel partChannel = FileChannel.open(partFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

          buffer.clear();
          int bytesReadInPart = sourceChannel.read(buffer);

          if (bytesReadInPart > 0) {
            buffer.flip();
            partChannel.write(buffer);
            bytesRead += bytesReadInPart;
            partFiles.add(partFile);
          }
        }
      }
    }
    return partFiles;
  }

  public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {

    Path outputFile = Paths.get(outputPath);

    for (Path part : partPaths) {
      if (!Files.exists(part)) {
        throw new IOException("Part file does not exist: " + part);
      }
    }

    try (FileChannel outputChannel = FileChannel.open(outputFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE))  {
      ByteBuffer byteBuffer = ByteBuffer.allocate(8192);

      for (Path part : partPaths) {
        try (FileChannel partChannel = FileChannel.open(part, StandardOpenOption.READ)) {
          long position = 0;
          long partSize = partChannel.size();

          while (position < partSize) {
            byteBuffer.clear();
            int bytesRead = partChannel.read(byteBuffer);
            if (bytesRead == -1) {
              break;
            }

            byteBuffer.flip();
            outputChannel.write(byteBuffer);
            position += bytesRead;
          }
        }
      }
    }
  }
}
