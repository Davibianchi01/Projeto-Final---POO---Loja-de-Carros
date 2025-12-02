package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String DB_NAME = "car_store";
    private static final String FULL_URL = URL + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Connection initialConnection = null;
            try {
                initialConnection = DriverManager.getConnection(URL + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", USER, PASSWORD);

                try (Statement stmt = initialConnection.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    System.out.println("Banco de dados '" + DB_NAME + "' verificado/criado com sucesso.");
                }

                initialConnection.close();

                connection = DriverManager.getConnection(FULL_URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Erro ao conectar/criar banco: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}