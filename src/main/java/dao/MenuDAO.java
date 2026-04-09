package dao;

import common.DBUtil;
import dto.Menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    // 가게별 전체 메뉴 조회
    public List<Menu> getMenusByStoreId(int storeId) {
        String sql = "SELECT menu_id, store_id, menu_name, price, is_available FROM menu WHERE store_id = ? ORDER BY menu_id";
        List<Menu> menus = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(new Menu(
                        rs.getInt("menu_id"),
                        rs.getInt("store_id"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menus;
    }
}
