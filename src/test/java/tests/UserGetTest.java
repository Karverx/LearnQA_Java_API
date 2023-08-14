package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


@Epic("Get user info cases")
@Feature("Get user info")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check get info - only username - User Not Auth")
    @DisplayName("Test positive get user info - only username - user not auth")
    @Test
    @Severity(value = SeverityLevel.CRITICAL)
    @TmsLink("test-case-getUserInfo-1")
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("This test check get info - username, firstName, lastName, email - auth as same user")
    @DisplayName("Test positive get user all info - auth as same user")
    @Test
    @Severity(value = SeverityLevel.CRITICAL)
    @TmsLink("test-case-getUserInfo-2")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/2",
                        header,
                        cookie
                );

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);

    }

    @Description("This test check get info - only username - auth as different user")
    @DisplayName("Test positive get user info - only username - auth as different user")
    @Test
    @Severity(value = SeverityLevel.CRITICAL)
    @TmsLink("test-case-getUserInfo-3")
    public void testGetUserDetailsAuthAsDifferentUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String urlAuth = "https://playground.learnqa.ru/api/user/"+ apiCoreRequests.getIdFromNewCreatingUser();
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        urlAuth,
                        header,
                        cookie
                );

        String[] unexpectedFields = {"firstName", "lastName", "email"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }
}
