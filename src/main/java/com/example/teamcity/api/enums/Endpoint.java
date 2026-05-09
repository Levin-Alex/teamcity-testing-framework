package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class, false),
    BUILD_QUEUE("/app/rest/buildQueue", Build.class, true),
    PROJECT("/app/rest/projects", Project.class, false),
    USERS("/app/rest/users", User.class, false);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
    /** Used only against WireMock; do not enqueue for superuser teardown. */
    private final boolean mocked;
}