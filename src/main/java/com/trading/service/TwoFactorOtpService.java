package com.trading.service;

import com.trading.modal.TwoFactorOtp;
import com.trading.modal.User;

public interface TwoFactorOtpService {

    TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt);

    TwoFactorOtp findByUser(Long userId);

    TwoFactorOtp findById(Long id);

    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp);

    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);

}
