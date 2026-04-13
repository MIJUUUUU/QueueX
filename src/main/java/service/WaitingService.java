package service;

import common.DBUtil;
import common.WaitingStatus;
import dao.MenuDAO;
import dao.OrderItemDAO;
import dao.StoreDAO;
import dao.WaitingDAO;
import dto.Menu;
import dto.Store;
import dto.Waiting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class WaitingService {

    private final StoreDAO storeDAO = new StoreDAO();
    private final MenuDAO menuDAO = new MenuDAO();
    private final WaitingDAO waitingDAO = new WaitingDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public List<Store> getAllStores() {
        return storeDAO.getAllStores();
    }

    public boolean hasActiveWaitingAtStore(int customerId, int storeId) {
        return waitingDAO.hasActiveWaitingAtStore(customerId, storeId);
    }

    public Store getStoreById(int storeId) {
        return storeDAO.findById(storeId);
    }

    public List<Menu> getMenusByStoreId(int storeId) {
        return menuDAO.getMenusByStoreId(storeId);
    }

    public boolean exceedsMaxCapacity(Store store, int peopleCount) {
        return store != null && peopleCount > store.getMaxCapacity();
    }

    public boolean exceedsMaxGroupSize(Store store, int peopleCount) {
        return store != null && peopleCount > store.getMaxGroupSize();
    }

    public int getTotalSelectedMenuQuantity(Map<Integer, Integer> selectedMenus) {
        int totalQuantity = 0;
        for (int quantity : selectedMenus.values()) {
            totalQuantity += quantity;
        }
        return totalQuantity;
    }

    // 해당 가게 현재 대기 목록 조회
    public List<Waiting> getWaitingByStoreId(int storeId) {
        return waitingDAO.findWaitingByStoreId(storeId);
    }

    // 대기 손님을 호출 상태로 변경
    public boolean callWaiting(int waitingId) {
        return waitingDAO.updateWaitingStatus(waitingId, WaitingStatus.CALLED);
    }

    // 입장 처리
    public boolean enterWaiting(int waitingId) {
        return waitingDAO.updateWaitingStatus(waitingId, WaitingStatus.ENTERED);
    }

    // 노쇼 처리
    public boolean noshowWaiting(int waitingId) {
        return waitingDAO.updateWaitingStatus(waitingId, WaitingStatus.NOSHOW);
    }

    // 대기 등록 + 선주문 항목 등록
    // selectedMenus: key=menuId, value=quantity
    public Waiting registerWaiting(int customerId, int storeId, int peopleCount, Map<Integer, Integer> selectedMenus) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Waiting waiting = waitingDAO.register(conn, customerId, storeId, peopleCount);

                for (Map.Entry<Integer, Integer> entry : selectedMenus.entrySet()) {
                    orderItemDAO.register(conn, waiting.getWaitingId(), entry.getKey(), entry.getValue());
                }

                conn.commit();
                return waiting;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return null;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 고객의 현재 대기 목록 조회
    public List<Waiting> getWaitingByCustomerId(int customerId) {
    return waitingDAO.findWaitingByCustomerId(customerId);
    }

    public List<String> getOrderSummariesByWaitingId(int waitingId) {
        return orderItemDAO.findOrderSummariesByWaitingId(waitingId);
    }

    public int getCurrentPosition(int storeId, int waitingNumber) {
        return waitingDAO.findCurrentPosition(storeId, waitingNumber);
    }

    public int getLanePosition(int storeId, int waitingNumber, int peopleCount) {
        return waitingDAO.findLanePosition(storeId, waitingNumber, peopleCount >= 6);
    }

    // 대기 취소
    public boolean cancelWaiting(int waitingId, int customerId) {
    return waitingDAO.cancelWaiting(waitingId, customerId);
    }

}
