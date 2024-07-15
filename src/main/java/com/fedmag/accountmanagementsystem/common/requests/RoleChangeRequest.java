package com.fedmag.accountmanagementsystem.common.requests;

import jakarta.validation.constraints.NotNull;

public record RoleChangeRequest(@NotNull String user, @NotNull String role, @NotNull String operation) {
}
