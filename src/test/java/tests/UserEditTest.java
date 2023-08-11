package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.getRegistrationData;

@Epic("Edit cases")
@Feature("Edit")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check edit username")
    @DisplayName("Test positive edit username")
    @Test
    public void testEditJustCreatedTest() {
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

        //EDIT

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie,
                        editData
                );

        //GET

        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Description("This test check edit user data by not auth user")
    @DisplayName("Test negative edit user by not auth user - Auth token not supplied")
    @Test
    public void testEditByNotAuthUser() {
        Map<String, String> userData = getRegistrationData();
        int userId = apiCoreRequests.getIdFromNewCreatingUser();
        String url = "https://playground.learnqa.ru/api/user/" + userId;

        Map<String, String> editData = new HashMap<>();
        editData.put("email", "NEW" + DataGenerator.getRandomEmail());
        editData.put("password", "NEW123");
        editData.put("username", "NEWlearnqa");
        editData.put("firstName", "NEWlearnqa");
        editData.put("lastName", "NEWlearnqa");

        //EDIT
        Response responseEditUser = apiCoreRequests.makePutRequest(
                url,
                null,
                null,
                editData
        );

        Assertions.assertResponseTextByEquals(responseEditUser, "Auth token not supplied");
        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        null,
                        null
                );
        Assertions.assertJsonByName(responseUserData, "username", userData.get("username"));
    }

    @Description("This test check edit user2 with token and cookie from user1")
    @DisplayName("Test edit user2 with token and cookie from user1")
    @Test
    public void testEditByDifferentUser() {
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


        //EDIT try edit user 2 with userId2 and with token and cookie from user1
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "NEW" + DataGenerator.getRandomEmail());
        editData.put("password", "NEW123");
        editData.put("username", "NEWlearnqa");
        editData.put("firstName", "NEWlearnqa");
        editData.put("lastName", "NEWlearnqa");

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId2,
                        token,
                        cookie,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 200);

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

        //Проверка, что user1 был отредактирован
        Assertions.assertJsonByName(responseUserData1, "username", editData.get("username"));
        Assertions.assertJsonByName(responseUserData1, "email", editData.get("email"));
        Assertions.assertJsonByName(responseUserData1, "firstName", editData.get("firstName"));
        Assertions.assertJsonByName(responseUserData1, "lastName", editData.get("lastName"));

        //Проверка, что user2 не был отредактирован
        Assertions.assertJsonByName(responseUserData2, "username", userData2.get("username"));
    }

    @Description("This test check edit user with incorrect email - no @")
    @DisplayName("Test negative edit user with incorrect email - no @")
    @Test
    public void testEditUserWithIncorrectEmail() {
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

        //EDIT

        String incorrectEmail = "learnqaexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", incorrectEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser,400);
        Assertions.assertResponseTextByEquals(responseEditUser,"Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie
                );

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Description("This test check edit user with short firstName - a")
    @DisplayName("Test negative edit user with short firstName - a")
    @Test
    public void testEditUserWithShortFirstName(){
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

        //EDIT

        String firstName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser,400);
        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie
                );

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
