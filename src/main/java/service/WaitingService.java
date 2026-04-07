package service;

import dao.MenuDAO;
import dao.OrderItemDAO;
import dao.StoreDAO;
import dao.WaitingDAO;
import dto.Menu;
import dto.Store;
import dto.Waiting;

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

    public List<Menu> getMenusByStoreId(int storeId) {
        return menuDAO.getMenusByStoreId(storeId);
    }

    // 대기 등록 + 선주문 항목 등록
    // selectedMenus: key=menuId, value=quantity
    public Waiting registerWaiting(int customerId, int storeId, int peopleCount, Map<Integer, Integer> selectedMenus) {
        Waiting waiting = waitingDAO.register(customerId, storeId, peopleCount);
        if (waiting == null) return null;

        for (Map.Entry<Integer, Integer> entry : selectedMenus.entrySet()) {
            orderItemDAO.register(waiting.getWaitingId(), entry.getKey(), entry.getValue());
        }
        return waiting;
    }
}
