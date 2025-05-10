package com.mehedi.spring_security_client.model;

public record UserModel(
        String firstName,
        String lastName,
        String email,
        String password,
        String matchingPassword
) {
}
