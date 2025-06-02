package ru.dulfi.db;

import java.sql.Connection;
import ru.dulfi.dao.DatabaseConnection;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection connection = DatabaseConnection.getConnection();
             var stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS owners (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    birth_date DATE NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pets (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    birth_date DATE NOT NULL,
                    breed VARCHAR(100) NOT NULL,
                    color VARCHAR(20) NOT NULL,
                    owner_id BIGINT REFERENCES owners(id)
                )
            """);

                stmt.execute("""
                CREATE TABLE IF NOT EXISTS pet_friends (
                    pet_id BIGINT REFERENCES pets(id),
                    friend_id BIGINT REFERENCES pets(id),
                    PRIMARY KEY (pet_id, friend_id)
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать базу данных", e);
        }
    }
} 