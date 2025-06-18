package com.trading.service;

import com.trading.modal.TwoFactorOtp;
import com.trading.modal.User;
import com.trading.repository.TwoFactorOtpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService{

    @Autowired
    private TwoFactorOtpRepo twoFactorOtpRepo;

    @Override
    public TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt) {

        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        TwoFactorOtp twoFactorOtp = new TwoFactorOtp();
        twoFactorOtp.setOtp(otp);
        twoFactorOtp.setJwt(jwt);
        twoFactorOtp.setId(id);
        twoFactorOtp.setUser(user);

        return twoFactorOtpRepo.save(twoFactorOtp);
    }

    @Override
    public TwoFactorOtp findByUser(Long userId) {

        return twoFactorOtpRepo.findByUserId(userId);
    }

    @Override
    public TwoFactorOtp findById(Long id) {
        Optional<TwoFactorOtp> otp= Optional.ofNullable(twoFactorOtpRepo.findByUserId(id));
        return otp.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp) {

        return twoFactorOtp.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp) {
     twoFactorOtpRepo.delete(twoFactorOtp);
    }
}
