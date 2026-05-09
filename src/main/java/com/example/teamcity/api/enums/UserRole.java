package com.example.teamcity.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_ADMIN("PROJECT_ADMIN");

    private final String roleId;
}