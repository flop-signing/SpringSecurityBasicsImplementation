package com.mehedi.spring_security_client.service;

import com.mehedi.spring_security_client.entity.PasswordResetToken;
import com.mehedi.spring_security_client.entity.UserEntity;
import com.mehedi.spring_security_client.entity.VerificationToken;
import com.mehedi.spring_security_client.model.UserModel;
import com.mehedi.spring_security_client.repository.PasswordResetTokenRepository;
import com.mehedi.spring_security_client.repository.UserRepository;
import com.mehedi.spring_security_client.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public UserEntity registerUser(UserModel user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(user.firstName());
        userEntity.setLastName(user.lastName());
        userEntity.setEmail(user.email());
        userEntity.setPassword(passwordEncoder.encode(user.password()));
        userEntity.setRole("USER");
        userRepository.save(userEntity);

        return userEntity;
    }

    @Override
    public void saveVerificationTokenForUser(String token, UserEntity user) {
        VerificationToken verificationToken = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Invalid verification token";
        }

        UserEntity user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0) {

            verificationTokenRepository.delete(verificationToken);
            return "Expired verification token";
        }

        user.setEnabled(true);

        userRepository.save(user);
        return "Valid";
    }

    @Override
    public VerificationToken generateVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void cretePasswordResetTokenForUser(UserEntity user,String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid verification token";
        }

        UserEntity user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (passwordResetToken.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0) {

            passwordResetTokenRepository.delete(passwordResetToken);
            return "Expired verification token";
        }


        return "Valid";
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    @Override
    public boolean checkIfValidOldPassword(UserEntity user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

}
