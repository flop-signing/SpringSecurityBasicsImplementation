package com.mehedi.spring_security_client.model;

public record PasswordModel(
        String email,
        String oldPassword,
        String newPassword
) {
}
