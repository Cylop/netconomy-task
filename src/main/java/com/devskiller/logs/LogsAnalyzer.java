package com.devskiller.logs;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Given a zip file, a search query, and a date range, count the number of occurrences of the search query in each file in
 * the zip file
 */
public class LogsAnalyzer {

  private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

  private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

  public static void unzip(File targetZipFilePath, File destinationFolderPath) {
    try {
      ZipFile zipFile = new ZipFile(targetZipFilePath);
      zipFile.extractAll(destinationFolderPath.toString());
    } catch (Exception e) {
      throw new IllegalStateException("Unable to unpack zip file");
    }
  }


  /**
   * Given a zip file, a search query, and a date range,
   * count the number of occurrences of the search query in each file in the zip file
   *
   * @param searchQuery The string to search for in the file.
   * @param zipFile The zip file to search in.
   * @param startDate The start date of the search.
   * @param numberOfDays The number of days to search for.
   * @return A map of file names and the number of occurrences of the search query in the file.
   */
  public Map<String, Integer> countEntriesInZipFile(
      String searchQuery, File zipFile, LocalDate startDate, Integer numberOfDays)
      throws IOException {
    HashMap<String, Integer> result = new HashMap<>();

    File targetDir = new File(TEMP_DIR, UUID.randomUUID().toString());
    unzip(zipFile, targetDir);

    LocalDate endDate =
        startDate.plusDays(
            numberOfDays
                - 1); // -1 because we dont want to see current date + numberOfDays, we want to see
                      // the next 3 days including the startDate

    return Files.walk(targetDir.toPath())
        .parallel()
        .map(Path::toFile)
        .filter(
            file -> {
              Matcher matcher = DATE_PATTERN.matcher(file.getName());
              if (!matcher.find()) return false;

              LocalDate fileDate = LocalDate.parse(matcher.group());

              return fileDate != null
                  && (fileDate.isEqual(startDate) || fileDate.isAfter(startDate))
                  && (fileDate.isEqual(endDate) || fileDate.isBefore(endDate));
            })
        .collect(
            Collectors.toMap(File::getName, file -> this.countOccurrenceInFile(file, searchQuery)));
  }

  /**
   * Count the number of occurrences of a search term in a file
   *
   * @param file The file to search.
   * @param searchTerm The string to search for in the file.
   * @return The number of occurrences of the search term in the file.
   */
  private int countOccurrenceInFile(File file, String searchTerm) {
    Stream<String> fileContentStream = null;

    try {
      fileContentStream = Files.lines(file.toPath());
    } catch (IOException ignored) {
    }

    return fileContentStream != null
        ? fileContentStream
            .parallel()
            .filter(line -> line.contains(searchTerm))
            .map(e -> 1)
            .reduce(0, Integer::sum)
        : 0;
  }
}
