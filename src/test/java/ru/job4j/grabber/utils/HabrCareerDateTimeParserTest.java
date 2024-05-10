package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    public void parseDateTimeString() {
        String time = "2024-05-03T18:27:26";
        HabrCareerDateTimeParser test = new HabrCareerDateTimeParser();

        LocalDateTime result = test.parse(time);

        assertThat(LocalDateTime.of(2024, 5, 3, 18, 27, 26)).isEqualTo(result);
    }

    @Test
    public void parseValidDateTimeString() {
        String dateTimeString = "2023-11-04T18:27:26";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();

        LocalDateTime result = parser.parse(dateTimeString);

        assertThat(LocalDateTime.of(2023, 11, 4, 18, 27, 26)).isEqualTo(result);
    }
}