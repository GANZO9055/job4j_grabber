package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    /**
     * Преобразует строку в формат LocalDateTime
     * по стандарту DateTimeFormatter.ISO_OFFSET_DATE_TIME
     * Важно!!! Стоит убедиться, что строка соответствует
     * стандарту DateTimeFormatter.ISO_OFFSET_DATE_TIME, иначе будет вызвано исключение.
     * Про остальные стандарты следует обратиться к документации DateTimeFormatter.
     * @param parse
     * @return LocalDateTime
     */
    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
