package service;

import common.PasswordUtil;
import common.PhoneNumberUtil;
import dao.CustomerDAO;
import dto.Customer;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    public Customer register(String phone, String password) {
        String normalized = PhoneNumberUtil.normalize(phone);
        if (customerDAO.findByPhone(normalized) != null) {
            return null;
        }

        return customerDAO.register(normalized, PasswordUtil.hashPassword(password));
    }

    // 로그인 처리
    // 반환값: 성공 시 Customer, 비밀번호 불일치 시 null
    public Customer login(String phone, String password) {
        String normalized = PhoneNumberUtil.normalize(phone);
        Customer found = customerDAO.findByPhone(normalized);

        if (found == null) {
            return null;
        }

        if (PasswordUtil.matches(password, found.getPassword())) {
            return found;
        }

        return null;
    }

    public boolean isRegistered(String phone) {
        return customerDAO.findByPhone(PhoneNumberUtil.normalize(phone)) != null;
    }

    public int getMaxPasswordAttempts() {
        return MAX_PASSWORD_ATTEMPTS;
    }

}
