package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.example.teamcity.api.enums.UserRole;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        CheckedRequests userCheckedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckedRequests.<Project>getRequest(PROJECT).create(testData.getProject());

        userCheckedRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var createdBuildType = userCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        CheckedRequests userCheckedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckedRequests.<Project>getRequest(PROJECT).create(testData.getProject());

        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        userCheckedRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES).create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errors[0].message", Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        superUserCheckRequests.<Project>getRequest(PROJECT).create(testData.getProject());
        
        testData.getUser().setRoles(Roles.builder()
                .role(List.of(Role.builder()
                        .roleId(UserRole.PROJECT_ADMIN.getRoleId())
                        .scope("p:" + testData.getProject().getId())
                        .build()))
                .build());
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckedRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var createdBuildType = userCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());
        
        softy.assertEquals(createdBuildType.getName(), testData.getBuildType().getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        superUserCheckRequests.<Project>getRequest(PROJECT).create(testData.getProject());
        
        testData.getUser().setRoles(Roles.builder()
                .role(List.of(Role.builder()
                        .roleId(UserRole.PROJECT_ADMIN.getRoleId())
                        .scope("p:" + testData.getProject().getId())
                        .build()))
                .build());
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        
        var secondProject = generate(Project.class);
        var secondUser = generate(User.class);
        secondUser.setRoles(Roles.builder()
                .role(List.of(Role.builder()
                        .roleId(UserRole.PROJECT_ADMIN.getRoleId())
                        .scope("p:" + secondProject.getId())
                        .build()))
                .build());
        superUserCheckRequests.<Project>getRequest(PROJECT).create(secondProject);
        superUserCheckRequests.getRequest(USERS).create(secondUser);
        
        var buildTypeOfProject1 = testData.getBuildType();
        new UncheckedBase(Specifications.authSpec(secondUser), BUILD_TYPES).create(buildTypeOfProject1)
                .then().assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(
                        "You do not have enough permissions to edit project with id: " + testData.getProject().getId()));
    }
}
