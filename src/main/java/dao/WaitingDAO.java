package dao;

import common.DBUtil;
import common.WaitingStatus;
import dto.Waiting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WaitingDAO {
    private static final String WAITING = WaitingStatus.WAITING;
    private static final String CALLED = WaitingStatus.CALLED;

    public boolean hasActiveWaitingAtStore(int customerId, int storeId) {
        String sql = """
            SELECT COUNT(*)
            FROM waiting
            WHERE customer_id = ?
              AND store_id = ?
              AND status IN ('WAITING', 'CALLED')
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setInt(2, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 대기 등록
    public Waiting register(int customerId, int storeId, int peopleCount) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Waiting waiting = register(conn, customerId, storeId, peopleCount);
                conn.commit();
                return waiting;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Waiting register(Connection conn, int customerId, int storeId, int peopleCount) throws SQLException {
        int waitingNumber = getNextWaitingNumber(conn, storeId);
        String sql = "INSERT INTO waiting (customer_id, store_id, waiting_number, people_count, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, storeId);
            pstmt.setInt(3, waitingNumber);
            pstmt.setInt(4, peopleCount);
            pstmt.setString(5, WAITING);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Waiting waiting = new Waiting();
                    waiting.setWaitingId(rs.getInt(1));
                    waiting.setCustomerId(customerId);
                    waiting.setStoreId(storeId);
                    waiting.setWaitingNumber(waitingNumber);
                    waiting.setPeopleCount(peopleCount);
                    waiting.setStatus(WAITING);
                    waiting.setCreatedAt(LocalDateTime.now());
                    return waiting;
                }
            }
        }

        throw new SQLException("대기 등록에 실패했습니다.");
    }

    private int getNextWaitingNumber(Connection conn, int storeId) throws SQLException {
        String insertSequenceSql = """
            INSERT INTO store_waiting_sequence (store_id, last_waiting_number)
            SELECT ?, COALESCE(MAX(waiting_number), 0)
            FROM waiting
            WHERE store_id = ?
            ON DUPLICATE KEY UPDATE last_waiting_number = last_waiting_number
            """;
        String selectSequenceSql = """
            SELECT last_waiting_number
            FROM store_waiting_sequence
            WHERE store_id = ?
            FOR UPDATE
            """;
        String updateSequenceSql = """
            UPDATE store_waiting_sequence
            SET last_waiting_number = ?
            WHERE store_id = ?
            """;

        try (PreparedStatement insertStmt = conn.prepareStatement(insertSequenceSql)) {
            insertStmt.setInt(1, storeId);
            insertStmt.setInt(2, storeId);
            insertStmt.executeUpdate();
        }

        int nextWaitingNumber;
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSequenceSql)) {
            selectStmt.setInt(1, storeId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("대기번호 시퀀스를 조회할 수 없습니다. store_id=" + storeId);
                }
                nextWaitingNumber = rs.getInt("last_waiting_number") + 1;
            }
        }

        try (PreparedStatement updateStmt = conn.prepareStatement(updateSequenceSql)) {
            updateStmt.setInt(1, nextWaitingNumber);
            updateStmt.setInt(2, storeId);
            updateStmt.executeUpdate();
        }

        return nextWaitingNumber;
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
        String expectedCurrentStatus;

        if (CALLED.equals(status)) {
            sql = "UPDATE waiting SET status = ?, called_at = NOW() WHERE waiting_id = ? AND status = ?";
            expectedCurrentStatus = WAITING;
        } else if (WaitingStatus.ENTERED.equals(status) || WaitingStatus.NOSHOW.equals(status)) {
            sql = "UPDATE waiting SET status = ? WHERE waiting_id = ? AND status = ?";
            expectedCurrentStatus = CALLED;
        } else {
            throw new IllegalArgumentException("지원하지 않는 상태 변경입니다: " + status);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, waitingId);
            pstmt.setString(3, expectedCurrentStatus);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 고객의 현재 WAITING/CALLED 상태 대기 조회
public List<Waiting> findWaitingByCustomerId(int customerId) {
    String sql = """
        SELECT waiting_id, customer_id, store_id, waiting_number, people_count, status, called_at, created_at
        FROM waiting
        WHERE customer_id = ? AND status IN ('WAITING', 'CALLED')
        ORDER BY created_at ASC
        """;

    List<Waiting> waitingList = new ArrayList<>();

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, customerId);

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

// 고객 본인 대기 취소 (WAITING 상태만 취소 가능)
public boolean cancelWaiting(int waitingId, int customerId) {
    String sql = "UPDATE waiting SET status = ? WHERE waiting_id = ? AND customer_id = ? AND status = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, WaitingStatus.CANCELED);
        pstmt.setInt(2, waitingId);
        pstmt.setInt(3, customerId);
        pstmt.setString(4, WaitingStatus.WAITING);

        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public int findCurrentPosition(int storeId, int waitingNumber) {
    String sql = """
        SELECT COUNT(*) + 1
        FROM waiting
        WHERE store_id = ?
          AND status IN ('WAITING', 'CALLED')
          AND waiting_number < ?
        """;

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, storeId);
        pstmt.setInt(2, waitingNumber);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return waitingNumber;
}

public int findLanePosition(int storeId, int waitingNumber, boolean groupLane) {
    String sql = """
        SELECT COUNT(*) + 1
        FROM waiting
        WHERE store_id = ?
          AND status IN ('WAITING', 'CALLED')
          AND waiting_number < ?
          AND people_count %s
        """.formatted(groupLane ? ">= 6" : "< 6");

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, storeId);
        pstmt.setInt(2, waitingNumber);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return waitingNumber;
}

}
