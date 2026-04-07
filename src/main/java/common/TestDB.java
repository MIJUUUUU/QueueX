package src.main.java.common;

import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection con = DBUtil.getConnection()) {
            System.out.println("DB 연결 성공!");
        } catch (Exception e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }
    }
}
