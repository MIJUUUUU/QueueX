package dao;

import common.DBUtil;
import common.WaitingStatus;
import dto.Waiting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WaitingDAO {

    // 해당 가게의 다음 대기 번호 계산
    public int getNextWaitingNumber(int storeId) {
        String sql = "SELECT COALESCE(MAX(waiting_number), 0) + 1 FROM waiting WHERE store_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // 대기 등록
    public Waiting register(int customerId, int storeId, int peopleCount) {
        int waitingNumber = getNextWaitingNumber(storeId);
        String sql = "INSERT INTO waiting (customer_id, store_id, waiting_number, people_count, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, customerId);
            pstmt.setInt(2, storeId);
            pstmt.setInt(3, waitingNumber);
            pstmt.setInt(4, peopleCount);
            pstmt.setString(5, WaitingStatus.WAITING);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Waiting waiting = new Waiting();
                    waiting.setWaitingId(rs.getInt(1));
                    waiting.setCustomerId(customerId);
                    waiting.setStoreId(storeId);
                    waiting.setWaitingNumber(waitingNumber);
                    waiting.setPeopleCount(peopleCount);
                    waiting.setStatus(WaitingStatus.WAITING);
                    return waiting;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 해당 가게 현재 WAITING 상태 손님 조회
    public List<Waiting> findWaitingByStoreId(int storeId) {
        String sql = """
        SELECT waiting_id, customer_id, store_id, waiting_number, people_count, status, called_at, created_at
        FROM waiting
        WHERE store_id = ? AND status = 'WAITING'
        ORDER BY waiting_number ASC
        """;

        List<Waiting> waitingList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    waitingList.add(new Waiting(
                        rs.getInt("waiting_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("store_id"),
                        rs.getInt("waiting_number"),
                        rs.getInt("people_count"),
                        rs.getString("status"),
                        rs.getTimestamp("called_at") != null ? rs.getTimestamp("called_at").toLocalDateTime() : null,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return waitingList;
    }
    // 대기 상태 변경
    public boolean updateWaitingStatus(int waitingId, String status) {
        String sql;

        if (WaitingStatus.CALLED.equals(status)) {
            sql = "UPDATE waiting SET status = ?, called_at = NOW() WHERE waiting_id = ?";
        } else {
            sql = "UPDATE waiting SET status = ? WHERE waiting_id = ?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, waitingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
