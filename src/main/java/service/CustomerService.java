package service;

import dao.CustomerDAO;
import dto.Customer;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    // 로그인(비밀번호 일치) 또는 신규 가입 처리
    // 반환값: 성공 시 Customer, 비밀번호 불일치 시 null
    public Customer login(String phone, String password) {
        Customer found = customerDAO.findByPhone(phone);

        // 전화번호 없음 → 신규 가입
        if (found == null) {
            return customerDAO.register(phone, password);
        }

        // 비밀번호 일치 → 로그인 성공
        if (found.getPassword().equals(password)) {
            return found;
        }

        // 비밀번호 불일치
        return null;
    }

    public boolean isRegistered(String phone) {
        return customerDAO.findByPhone(phone) != null;
    }

    public int getMaxPasswordAttempts() {
        return MAX_PASSWORD_ATTEMPTS;
    }
}
