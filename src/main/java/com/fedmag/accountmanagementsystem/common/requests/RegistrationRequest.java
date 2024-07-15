package com.fedmag.accountmanagementsystem.common.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
    String name,
    String lastname,

    @NotNull
    @Pattern(regexp = "@acme.com", message = "only acme emails are allowed.")
    String email,

    @Size(min = 12, message = "password must be longer than 12 chars.") String password,
    String authority
) {}
