package com.devskiller.logs;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.lingala.zip4j.ZipFile;

public class LogsAnalyzer {

	private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

	public Map<String, Integer> countEntriesInZipFile(String searchQuery, File zipFile, LocalDate startDate, Integer numberOfDays) throws IOException {
		HashMap<String, Integer> result = new HashMap<>();

		File targetDir = new File(TEMP_DIR, UUID.randomUUID().toString());
		unzip(zipFile, targetDir);

		// TODO: Implement

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
