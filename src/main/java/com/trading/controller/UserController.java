package com.trading.controller;

import com.trading.domain.VerificationType;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;
import com.trading.service.EmailService;
import com.trading.service.UserService;
import com.trading.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/api/users/profile")
   public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
       User user = userService.findUserProfileByJwt(jwt);

       return  new ResponseEntity<User>(user, HttpStatus.OK);
   }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerification(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType
            ) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode==null){
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(),verificationCode.getOtp());
        }
        return  new ResponseEntity<>("Verification Otp Send Successfully", HttpStatus.OK);
    }

   @PostMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
                verificationCode.getEmail():verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if(isVerified){
            User upadteUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(),sendTo,user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);

        return new ResponseEntity<>(upadteUser, HttpStatus.OK);

        }
       throw new Exception("Wrong Otp");
    }
}
