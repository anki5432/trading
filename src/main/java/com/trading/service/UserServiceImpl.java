package com.trading.service;

import com.trading.config.JwtProvider;
import com.trading.domain.VerificationType;
import com.trading.modal.TwoFactorAuth;
import com.trading.modal.User;
import com.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * @param jwt
     * @return
     */
    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not found");
        }
        return user;
    }

    /**
     * @param email
     * @return
     */
    @Override
    public User findByUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not found");
        }
        return user;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public User findByUserId(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()){
            throw new Exception("User Not Found");
        }
        return user.get();
    }

    /**
     * @param user
     * @return
     */
    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);

        return userRepository.save(user);
    }

    /**
     * @param user
     * @param newPassword
     * @return
     */
    @Override
    public User updatePassword(User user, String newPassword) {
       user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
