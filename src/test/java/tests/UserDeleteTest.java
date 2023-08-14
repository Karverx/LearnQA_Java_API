package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.getRegistrationData;

@Epic("Deletion cases")
@Feature("Delete")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check delete user with id 2")
    @DisplayName("Test negatives delete user with id 2")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("test-case-delete-1")
    public void testDeleteUserWithId2() {

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        int userId = this.getIntFromJson(responseGetAuth, "user_id");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextByEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("This test check delete user")
    @DisplayName("Test positive delete user")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("test-case-delete-2")
    public void testDeleteUserSuccessfully() {
        //GENERATE USER
        Map<String, String> userData = getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );

        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE USER
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie
                );
        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie
                );

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextByEquals(responseUserData, "User not found");
    }

    @Description("This test check delete user2 by user1")
    @DisplayName("Test negative delete user2 by user1")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Issue(value = "BUG-3447")
    @TmsLink("test-case-delete-3")
    public void testDeleteUserByDifferentUser() {
        //GENERATE USER1 and USER2
        Map<String, String> userData1 = getRegistrationData();

        Response responseCreateAuth1 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData1
                );

        Map<String, String> userData2 = getRegistrationData();

        Response responseCreateAuth2 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData2
                );

        String userId1 = responseCreateAuth1.jsonPath().getString("id");
        String userId2 = responseCreateAuth2.jsonPath().getString("id");

        //LOGIN by user1
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        //DELETE try delete user 2 with userId2 and with token and cookie from user1
        Response responseDeleteUser2 = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId2,
                        token,
                        cookie
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser2, 200);

        //GET
        Response responseUserData1 = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId1,
                        token,
                        cookie
                );

        Response responseUserData2 = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId2,
                        token,
                        cookie
                );

        //Проверка, что удален user1
        Assertions.assertResponseTextByEquals(responseUserData1, "User not found");

        //Проверка, что не удален user2
        Assertions.assertJsonByName(responseUserData2, "username", userData2.get("username"));

    }
}
