package com.mehedi.spring_security_client.service;

import com.mehedi.spring_security_client.entity.UserEntity;
import com.mehedi.spring_security_client.entity.VerificationToken;
import com.mehedi.spring_security_client.model.UserModel;

import java.util.Optional;

public interface UserService {
    UserEntity registerUser(UserModel user);

    void saveVerificationTokenForUser(String token,UserEntity user);

    String validateVerificationToken(String token);

    VerificationToken generateVerificationToken(String oldToken);

    UserEntity findUserByEmail(String email);

    void cretePasswordResetTokenForUser(UserEntity user,String token);

    String validatePasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    void changePassword(UserEntity userEntity, String s);

    boolean checkIfValidOldPassword(UserEntity user, String s);
}
