package dao;

import common.DBUtil;
import dto.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItemDAO {

    // 선주문 항목 등록
    public void register(int waitingId, int menuId, int quantity) {
        String sql = "INSERT INTO order_item (waiting_id, menu_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, waitingId);
            pstmt.setInt(2, menuId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
