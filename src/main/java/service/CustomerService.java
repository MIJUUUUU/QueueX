package service;

import common.PasswordUtil;
import common.PhoneNumberUtil;
import dao.CustomerDAO;
import dto.Customer;

public class CustomerService {
    

    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    // 로그인(비밀번호 일치) 또는 신규 가입 처리
    // 반환값: 성공 시 Customer, 비밀번호 불일치 시 null
    public Customer login(String phone, String password) {
        String normalized = PhoneNumberUtil.normalize(phone);
        Customer found = customerDAO.findByPhone(normalized);


        // 전화번호 없음 → 신규 가입
        if (found == null) {
            return customerDAO.register(normalized, PasswordUtil.hashPassword(password));
        }

        // 비밀번호 일치 → 로그인 성공
        if (PasswordUtil.matches(password, found.getPassword())) {
            return found;
        }

        // 비밀번호 불일치
        return null;
    }

  
    public boolean isRegistered(String phone) {
        return customerDAO.findByPhone(PhoneNumberUtil.normalize(phone)) != null;
    }

    public int getMaxPasswordAttempts() {
        return MAX_PASSWORD_ATTEMPTS;
    }

}
