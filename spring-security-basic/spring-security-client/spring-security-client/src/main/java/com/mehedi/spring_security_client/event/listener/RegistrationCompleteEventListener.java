package com.mehedi.spring_security_client.event.listener;

import com.mehedi.spring_security_client.entity.UserEntity;
import com.mehedi.spring_security_client.event.RegistrationCompleteEvent;
import com.mehedi.spring_security_client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private UserService userService;

    public RegistrationCompleteEventListener(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // Create the Verification Token for the User.

        UserEntity userEntity=event.getUserEntity();
        String token= UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,userEntity);

        // Send Mail to the User.
        String url=event.getApplicationUrl()+"/verifyRegistration?token="+token;

        // Send Email Verification method.
        log.info("Click the link below to verify your registration: {}",url);


    }
}
