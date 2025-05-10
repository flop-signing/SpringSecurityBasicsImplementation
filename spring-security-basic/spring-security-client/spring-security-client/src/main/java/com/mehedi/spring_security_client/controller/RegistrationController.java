package com.mehedi.spring_security_client.controller;

import com.mehedi.spring_security_client.entity.UserEntity;
import com.mehedi.spring_security_client.entity.VerificationToken;
import com.mehedi.spring_security_client.event.RegistrationCompleteEvent;
import com.mehedi.spring_security_client.model.PasswordModel;
import com.mehedi.spring_security_client.model.UserModel;
import com.mehedi.spring_security_client.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {

    private final UserService userService;
    private ApplicationEventPublisher eventPublisher;


    public RegistrationController(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/register-user")
    public String registerUser(@RequestBody UserModel user, final HttpServletRequest request) {
        UserEntity userEntity = userService.registerUser(user);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(userEntity, applicationUrl(request)));


        return "Successfully registered!";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User successfully verified!";
        }
        return "Bad User Request!";

    }


    @GetMapping("/resend-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {

        VerificationToken verificationToken = userService.generateVerificationToken(oldToken);

        UserEntity user = verificationToken.getUser();

        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);

        return "Verification Link has been sent!";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        UserEntity user = userService.findUserByEmail(passwordModel.email());
        String url = "";

        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.cretePasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }


    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {

        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token!";
        }

        Optional<UserEntity> user = userService.getUserByPasswordResetToken(token);

        if (user.isPresent()) {

            userService.changePassword(user.get(), passwordModel.newPassword());
            return "Password Reset Successful!";
        } else {
            return "Invalid Token!";
        }

    }


    @PostMapping("/change-password")
    public String changePassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        UserEntity user= userService.findUserByEmail(passwordModel.email());

        if(userService.checkIfValidOldPassword(user,passwordModel.oldPassword())){
            return "Invalid Old Password!";
        }


        // Save new password functionality
        userService.changePassword(user, passwordModel.newPassword());

        return "Password Changed Successful!";
    }



    private String passwordResetTokenMail(UserEntity user, String applicationUrl, String token) {

        // Send Mail to the User.
        String url = applicationUrl + "/savePassword?token=" + token;

        // Send Email Verification method.
        log.info("Click the link below to reset your password : {}", url);

        return url;

    }

    private void resendVerificationTokenMail(UserEntity user, String applicationUrl, VerificationToken verificationToken) {

        // Send Mail to the User.
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        // Send Email Verification method.
        log.info("Click the link below to verify your registration: {}", url);

    }


    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }


}
