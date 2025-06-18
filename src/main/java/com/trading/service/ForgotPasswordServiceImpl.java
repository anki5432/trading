package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPasswordToken;
import com.trading.modal.User;
import com.trading.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

   @Autowired
   private ForgotPasswordRepository forgotPasswordRepository;


    /**
     * @param user
     * @param id
     * @param otp
     * @param verificationType
     * @param sendTo
     * @return
     */
    @Override
    public ForgotPasswordToken createToken(User user,
                                           String id,
                                           String otp,
                                           VerificationType verificationType,
                                           String sendTo) {

        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setSendTo(sendTo);
        forgotPasswordToken.setVerificationType(verificationType);
        forgotPasswordToken.setOtp(otp);
        forgotPasswordToken.setId(id);

        return forgotPasswordRepository.save(forgotPasswordToken);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token = forgotPasswordRepository.findById(id);
        return token.orElse(null);
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return null;
    }

    /**
     * @param token
     */
    @Override
    public void deleteToken(ForgotPasswordToken token) {

    }
}
