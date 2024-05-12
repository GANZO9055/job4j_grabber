package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) {
        try (InputStream input = PsqlStore.class.getClassLoader()
                .getResourceAsStream("db/rabbit.properties")) {
            config.load(input);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try {
            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                                    INSERT INTO post (name, text, link, created)
                                    VALUES (?, ?, ?, ?)
                                    ON CONFLICT (link)
                                    DO NOTHING
                                    """,
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setId(generatedKeys.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM post");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                posts.add(createPost(result));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM post WHERE id = ?");
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                post = createPost(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    private Post createPost(ResultSet resultSet) {
        try {
            return new Post(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("link"),
                    resultSet.getString("text"),
                    resultSet.getTimestamp("created").toLocalDateTime());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        try {
            PsqlStore psqlStore = new PsqlStore(new Properties());
            psqlStore.save(new Post("test1", "test_21", "test3_3", LocalDateTime.now()));
            psqlStore.save(new Post("test2", "test_22", "test3_3", LocalDateTime.now()));
            psqlStore.save(new Post("test3", "test_23", "test3_3", LocalDateTime.now()));
            psqlStore.save(new Post("test4", "test_24", "test3_3", LocalDateTime.now()));
            System.out.println(psqlStore.getAll());
            System.out.println(psqlStore.findById(2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
