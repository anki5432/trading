package com.trading.repository;

import com.trading.modal.TwoFactorOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoFactorOtpRepo extends JpaRepository<TwoFactorOtp, String> {

    TwoFactorOtp findByUserId(Long userId);
}

