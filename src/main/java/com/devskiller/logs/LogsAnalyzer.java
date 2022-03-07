package com.devskiller.logs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.lingala.zip4j.ZipFile;

public class LogsAnalyzer {

	private final static Pattern DATE_PATTERN = Pattern.compile(
			"(\\d{4}-\\d{2}-\\d{2})");

	private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

	public Map<String, Integer> countEntriesInZipFile(String searchQuery, File zipFile, LocalDate startDate, Integer numberOfDays) throws IOException {
		HashMap<String, Integer> result = new HashMap<>();

		File targetDir = new File(TEMP_DIR, UUID.randomUUID().toString());
		unzip(zipFile, targetDir);

		LocalDate endDate = startDate.plusDays(numberOfDays);

		// TODO: Implement
		Stream<File> filteredFiles = Files.walk(targetDir.toPath())
				.map(Path::toFile)
				.filter(file -> {
					Matcher matcher = DATE_PATTERN.matcher(file.getName());
					if(!matcher.find()) return false;

					LocalDate fileDate = LocalDate.parse(matcher.group());

					return fileDate != null && fileDate.isAfter(startDate) && fileDate.isBefore(endDate);
				});


		return result;
	}

	public static void unzip(File targetZipFilePath, File destinationFolderPath) {
		try {
			ZipFile zipFile = new ZipFile(targetZipFilePath);
			zipFile.extractAll(destinationFolderPath.toString());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to unpack zip file");
		}
	}

}
