package common;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3308/queuex?serverTimezone=Asia/Seoul";
    private static final String USER = "root";
    private static String PASSWORD = "11111111";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

