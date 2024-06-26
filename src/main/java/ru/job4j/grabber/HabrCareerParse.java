package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;
    private static final int MAXIMUM_NUMBER_OF_PAGES = 5;
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        return document.select(".vacancy-description__text").text();
    }

    /**
     * Происходит считывание первых пяти страниц (MAXIMUM_NUMBER_OF_PAGES),
     * во время считывания данные записываются в объект Post и вносятся в список List
     * @param link
     * @return List<Post>
     */
    @Override
    public List<Post> list(String link) {
        int pageNumber = 1;
        List<Post> posts = new ArrayList<>();
        while (pageNumber++ <= MAXIMUM_NUMBER_OF_PAGES) {
            String fullLink = "%s%s%d%s".formatted(link, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = null;
            try {
                document = connection.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                String date = dateElement.child(0).attr("datetime");
                LocalDateTime dateTime = dateTimeParser.parse(date);
                String vacancyName = titleElement.text();
                String linkVacation = String.format("%s%s", link, linkElement.attr("href"));
                try {
                    posts.add(new Post(vacancyName,
                            linkVacation,
                            retrieveDescription(linkVacation),
                            dateTime));
                    System.out.printf("%s %s%n %s%s%n", vacancyName, linkVacation,
                            retrieveDescription(linkVacation), date);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return posts;
    }
}