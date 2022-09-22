package com.devskiller.logs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LogsAnalyzerTest {

  private File zipPath;
  private Map<String, Integer> entries;

  @BeforeEach
  public void setUp() throws Exception {
    zipPath =
        Paths.get(getClass().getClassLoader().getResource("logs-27_02_2018-03_03_2018.zip").toURI())
            .toFile();
  }

  @Test
  public void shouldContainEntriesForCorrectDays() throws IOException {
    // given
    LogsAnalyzer logsAnalyzer = new LogsAnalyzer();

    // when
    entries = logsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, LocalDate.of(2018, 2, 27), 3);

    // then
    assertThat(entries)
        .hasSize(3)
        .containsKey("logs_2018-03-01-access.log")
        .containsKey("logs_2018-02-28-access.log")
        .containsKey("logs_2018-02-27-access.log");
  }

  @Test
  public void shouldReturnLineCountsForMozilla() throws IOException {
    // given
    LogsAnalyzer logsAnalyzer = new LogsAnalyzer();

    // when
    entries = logsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, LocalDate.of(2018, 2, 27), 3);

    // then
    assertThat(entries)
        .hasSize(3)
        .containsEntry("logs_2018-03-01-access.log", 23)
        .containsEntry("logs_2018-02-28-access.log", 18)
        .containsEntry("logs_2018-02-27-access.log", 40);
  }

  @Test
  public void shouldReturnLineCountsForSafari() throws IOException {
    // given
    LogsAnalyzer logsAnalyzer = new LogsAnalyzer();

    // when
    entries = logsAnalyzer.countEntriesInZipFile("Safari", zipPath, LocalDate.of(2018, 2, 27), 4);

    // then
    assertThat(entries)
        .hasSize(4)
        .containsEntry("logs_2018-03-02-access.log", 6)
        .containsEntry("logs_2018-03-01-access.log", 16)
        .containsEntry("logs_2018-02-28-access.log", 14)
        .containsEntry("logs_2018-02-27-access.log", 25);
  }
}
