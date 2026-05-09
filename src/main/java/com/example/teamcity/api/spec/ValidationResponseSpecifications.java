package com.example.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public final class ValidationResponseSpecifications {

    private ValidationResponseSpecifications() {
    }

    public static ResponseSpecification checkBuildTypeIdAlreadyUsed(String buildTypeId) {
        var b = new ResponseSpecBuilder();
        b.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        b.expectBody("errors[0].message", Matchers.containsString(
                "The build configuration / template ID \"%s\" is already used by another configuration or template"
                        .formatted(buildTypeId)));
        return b.build();
    }

    public static ResponseSpecification checkForbiddenToEditProject(String projectId) {
        var b = new ResponseSpecBuilder();
        b.expectStatusCode(HttpStatus.SC_FORBIDDEN);
        b.expectBody(Matchers.containsString(
                "You do not have enough permissions to edit project with id: " + projectId));
        return b.build();
    }
}
