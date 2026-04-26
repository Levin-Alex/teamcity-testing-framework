package com.example.teamcity.api;

import com.teamcity.api.models.Build;
import com.teamcity.api.models.Property;
import com.example.teamcity.api.models.Steps;
import com.teamcity.api.requests.checked.CheckedBase;
import com.teamcity.api.spec.Specifications;
import com.example.teamcity.common.WireMock;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.apache.http.HttpStatus;
import org.availability.Availability;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.automaker.wiremock.client.WireMock.post;
import static com.teamcity.api.enums.Endpoint.BUILDS;
import static com.teamcity.api.enums.Endpoint.BUILD_QUEUE;
import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {
    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with WireMock)", groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.getSpec()
                .mockSpec(), BUILD_QUEUE);

        var build = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());

        softy.assertThat(build.getState()).as("buildState").isEqualTo("finished");
        softy.assertThat(build.getStatus()).as("buildStatus").isEqualTo("SUCCESS");
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}