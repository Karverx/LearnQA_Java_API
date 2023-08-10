package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test check registration new user with existing email")
    @DisplayName("Test negative register user - existing email")
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextByEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    @Test
    @Description("This test successfully register new user")
    @DisplayName("Test positive register user - Success")
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        System.out.println(responseCreateAuth.asString());
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test check registration new user with incorrect email - no @ in email")
    @DisplayName("Test negative register user - no @ in email")
    public void testCreateUserWithIncorrectEmail(){
        String email = "learnqaexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextByEquals(responseCreateAuth,"Invalid email format");
    }

    @Description("This test check registration new user without some of field in body")
    @DisplayName("Test negative register user - one missing field")
    @ParameterizedTest
    @CsvSource(value = {
            "'{\"username\": \"test\",\"password\": \"test\",\"firstName\": \"test\",\"lastName\": \"test\"}', email",
            "'{\"email\": \"test@mail.com\",\"password\": \"test\",\"firstName\": \"test\",\"lastName\": \"test\"}', username",
            "'{\"email\": \"test@mail.com\",\"username\": \"test\",\"firstName\": \"test\",\"lastName\": \"test\"}', password",
            "'{\"email\": \"test@mail.com\",\"username\": \"test\",\"password\": \"test\",\"lastName\": \"test\"}', firstName",
            "'{\"email\": \"test@mail.com\",\"username\": \"test\",\"password\": \"test\",\"firstName\": \"test\"}', lastName"
    })
    public void testCreateUserWithIncorrectData(String body, String missing) throws JsonProcessingException {

        HashMap userData =
                new ObjectMapper().readValue(body, HashMap.class);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextByEquals(responseCreateAuth, "The following required params are missed: " + missing);
    }


    @Test
    @Description("This test check registration new user with incorrect name - short")
    @DisplayName("Test negative register user - short username = a")
    public void testCreateUserWithShortUsername(){
        String name = "a";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextByEquals(responseCreateAuth,"The value of 'username' field is too short");

    }

    @Test
    @Description("This test check registration new user with incorrect name - very long username > 250 symbols")
    @DisplayName("Test negative register user - very long username > 250 symbols")
    public void testCreateUserWithVeryLongUsername(){
        String name = RandomStringUtils.randomAlphabetic(251);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user",
                        userData
                );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextByEquals(responseCreateAuth,"The value of 'username' field is too long");
    }
}
