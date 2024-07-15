package com.fedmag.accountmanagementsystem.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePassRequest(@JsonProperty("new_password") String newPassword) {
}
