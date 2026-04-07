package dao;

import common.DBUtil;
import dto.Admin;
import dto.Store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

  public List<Admin> findAllAdmins() {
    String sql = "SELECT admin_id, admin_auth_code FROM admin";
    List<Admin> admins = new ArrayList<>();

    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()
    ) {
      while (rs.next()) {
        admins.add(new Admin(
            rs.getInt("admin_id"),
            rs.getString("admin_auth_code")
        ));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return admins;
  }

  public List<Store> findStoresByAdminId(int adminId) {
    String sql = "SELECT store_id, admin_id, store_name, category FROM store WHERE admin_id = ?";
    List<Store> stores = new ArrayList<>();

    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, adminId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          stores.add(new Store(
              rs.getInt("store_id"),
              rs.getInt("admin_id"),
              rs.getString("store_name"),
              rs.getString("category")
          ));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return stores;
  }
}