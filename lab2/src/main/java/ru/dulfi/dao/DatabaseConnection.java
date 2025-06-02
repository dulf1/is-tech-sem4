package ru.dulfi.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти файл database.properties");
            }
            properties.load(input);
            
            Class.forName("org.postgresql.Driver");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации базы данных", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC не найден", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("db.url"),
            properties.getProperty("db.username"),
            properties.getProperty("db.password")
        );
    }
} 