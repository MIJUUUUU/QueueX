package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = System.getenv().getOrDefault(
            "QUEUEX_DB_URL",
            "jdbc:mysql://localhost:3308/queuex?serverTimezone=Asia/Seoul"
    );
    private static final String USER = System.getenv().getOrDefault("QUEUEX_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("QUEUEX_DB_PASSWORD", "11111111");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
