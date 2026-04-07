package dao;

import common.DBUtil;
import dto.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAO {
  // 관리자/대기/통계 관련 SQL
  public Admin findAdmin(String adminAuthCode) {
    String sql = "SELECT admin_id, admin_auth_code FROM admin WHERE admin_auth_code = ?";

    try {
      Connection conn = DBUtil.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, adminAuthCode);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return new Admin(
            rs.getInt("admin_id"),
            rs.getString("admin_auth_code")
        );
      }


    } catch (Exception e) {
      e.printStackTrace();

    }
    return null;
  }
}
