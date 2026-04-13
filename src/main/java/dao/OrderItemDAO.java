package dao;

import common.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    // 선주문 항목 등록
    public void register(int waitingId, int menuId, int quantity) {
        try (Connection conn = DBUtil.getConnection()) {
            register(conn, waitingId, menuId, quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void register(Connection conn, int waitingId, int menuId, int quantity) throws SQLException {
        String sql = "INSERT INTO order_item (waiting_id, menu_id, quantity) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, waitingId);
            pstmt.setInt(2, menuId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        }
    }

    public List<String> findOrderSummariesByWaitingId(int waitingId) {
        String sql = """
            SELECT m.menu_name, oi.quantity
            FROM order_item oi
            JOIN menu m ON m.menu_id = oi.menu_id
            WHERE oi.waiting_id = ?
            ORDER BY oi.order_item_id ASC
            """;

        List<String> orderSummaries = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, waitingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderSummaries.add(rs.getString("menu_name") + " " + rs.getInt("quantity") + "개");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderSummaries;
    }
}
