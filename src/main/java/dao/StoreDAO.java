package dao;

import common.DBUtil;
import dto.Store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreDAO {

    // 전체 가게 목록 조회
    public List<Store> getAllStores() {
        String sql = "SELECT store_id, admin_id, store_name, category FROM store";
        List<Store> stores = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                stores.add(new Store(
                    rs.getInt("store_id"),
                    rs.getInt("admin_id"),
                    rs.getString("store_name"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }
}
