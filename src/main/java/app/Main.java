package app;

import common.DBUtil;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("QueueX application started.");

        try (Connection connection = DBUtil.getConnection()) {
            System.out.println("DB 연결 성공!");
        } catch (Exception e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }
    }
}
