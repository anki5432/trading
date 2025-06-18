package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws Exception;

    public  User findByUserByEmail(String email) throws Exception;

    public  User findByUserId(Long userId) throws Exception;

    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);

    User updatePassword(User user, String newPassword);
}
