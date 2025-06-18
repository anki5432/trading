package com.trading.controller;

import com.trading.config.JwtProvider;
import com.trading.modal.TwoFactorOtp;
import com.trading.modal.User;
import com.trading.repository.UserRepository;
import com.trading.response.AuthResponse;
import com.trading.service.CustomeUserDetailsService;
import com.trading.service.EmailService;
import com.trading.service.TwoFactorOtpService;
import com.trading.utils.OtpUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomeUserDetailsService customeUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist= userRepository.findByEmail(user.getEmail());
        if(isEmailExist!=null){
            throw  new Exception("email is already exist for for another account");
        }
        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        User saveUser = userRepository.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail()
                ,user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Register success");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(userName,password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        User authUser= userRepository.findByEmail(userName);
        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor auth enable");
            res.setTwoFactorAuthEnable(true);
            String otp = OtpUtlis.genrateOTP();

            TwoFactorOtp oldTwoFactorOtp = twoFactorOtpService.findByUser(authUser.getId());
            if (oldTwoFactorOtp!=null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }
            TwoFactorOtp newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt);

            emailService.sendVerificationOtpEmail(userName,otp);

            res.setSession(newTwoFactorOtp.getId());
            return  new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }
        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("login success");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = customeUserDetailsService.loadUserByUsername(userName);
        if (userDetails == null) {
            throw new BadCredentialsException("invalid username and password");
        }
        if (!password.equals((userDetails.getPassword()))) {
                throw new BadCredentialsException("invalid password");
            }
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigingOtp(@PathVariable String otp, @RequestParam String id) throws Exception {
      TwoFactorOtp twoFactorOtp = twoFactorOtpService.findById(Long.valueOf(id));
 if( twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp,otp)){
AuthResponse response = new AuthResponse();
response.setMessage("two Factor Authentication Verified ");
response.setTwoFactorAuthEnable(true);
response.setJwt(twoFactorOtp.getJwt());

return new ResponseEntity<>(response, HttpStatus.OK);
 }
throw  new Exception("invalid otp");
    }
}
