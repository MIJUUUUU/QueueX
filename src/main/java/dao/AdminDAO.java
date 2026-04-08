package dao;

import common.DBUtil;
import dto.Admin;
import dto.AdminStatistics;
import dto.MenuStat;
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

  public AdminStatistics getTodayStatisticsByStoreId(int storeId) {
    int totalVisitedCustomers = getTodayVisitedCustomers(storeId);
    int totalWaitingCount = getTodayWaitingCount(storeId);
    int noShowCount = getTodayNoShowCount(storeId);
    List<MenuStat> menuStats = getTodayMenuStats(storeId);

    return new AdminStatistics(totalVisitedCustomers, totalWaitingCount, noShowCount, menuStats);
  }

  private int getTodayVisitedCustomers(int storeId) {
    String sql = """
        SELECT COALESCE(SUM(people_count), 0)
        FROM waiting
        WHERE store_id = ? AND status = 'ENTERED' AND DATE(created_at) = CURDATE()
        """;

    return getSingleCount(sql, storeId);
  }

  private int getTodayWaitingCount(int storeId) {
    String sql = """
        SELECT COUNT(*)
        FROM waiting
        WHERE store_id = ? AND DATE(created_at) = CURDATE()
        """;

    return getSingleCount(sql, storeId);
  }

  private int getTodayNoShowCount(int storeId) {
    String sql = """
        SELECT COUNT(*)
        FROM waiting
        WHERE store_id = ? AND status = 'NOSHOW' AND DATE(created_at) = CURDATE()
        """;

    return getSingleCount(sql, storeId);
  }

  private int getSingleCount(String sql, int storeId) {
    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, storeId);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  private List<MenuStat> getTodayMenuStats(int storeId) {
    String sql = """
        SELECT m.menu_name, COALESCE(SUM(oi.quantity), 0) AS order_count
        FROM menu m
        LEFT JOIN waiting w
          ON w.store_id = ?
          AND DATE(w.created_at) = CURDATE()
        LEFT JOIN order_item oi
          ON oi.waiting_id = w.waiting_id
          AND oi.menu_id = m.menu_id
        WHERE m.store_id = ?
        GROUP BY m.menu_id, m.menu_name
        ORDER BY order_count DESC, m.menu_id ASC
        """;

    List<MenuStat> menuStats = new ArrayList<>();

    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, storeId);
      pstmt.setInt(2, storeId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          menuStats.add(new MenuStat(
              rs.getString("menu_name"),
              rs.getInt("order_count")
          ));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return menuStats;
  }
}
