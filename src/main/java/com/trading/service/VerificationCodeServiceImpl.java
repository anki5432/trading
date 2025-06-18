package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;
import com.trading.repository.VerificationCodeRepository;
import com.trading.utils.OtpUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService{

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    /**
     * @return
     */
    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
      VerificationCode verificationCode1 = new VerificationCode();
      verificationCode1.setOtp(OtpUtlis.genrateOTP());
      verificationCode1.setVerificationType(verificationType);
      verificationCode1.setUser(user);

    return verificationCodeRepository.save(verificationCode1);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode= verificationCodeRepository.findById(id);

        if(verificationCode.isPresent()){
            return verificationCode.get();
        }

        throw new Exception("Verification Code Not Found");
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    /**
     * @param verificationCode
     */
    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
       verificationCodeRepository.delete(verificationCode);
    }
}
