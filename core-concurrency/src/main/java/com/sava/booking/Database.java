package com.sava.booking;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/booking_system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        // Pool tuning
        config.setMaximumPoolSize(20);      // max DB connections
        config.setMinimumIdle(5);           // keep 5 ready
        config.setIdleTimeout(30000);       // 30 sec
        config.setConnectionTimeout(30000); // wait max 30 sec for connection

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
