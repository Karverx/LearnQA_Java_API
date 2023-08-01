import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Task11TestCookie {
    @Test
    public void testCookie() {
        Response response = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> responseCookies = response.getCookies();
        //System.out.println(responseCookies);

        assertEquals(responseCookies.get("HomeWork"), "hw_value");

    }
}
