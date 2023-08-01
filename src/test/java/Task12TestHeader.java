import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Task12TestHeader {
    @Test
    public void testHeader() {
        Response response = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers responseHeaders = response.getHeaders();

        assertEquals(responseHeaders.get("x-secret-homework-header").toString(), "x-secret-homework-header=Some secret value");
        assertEquals(responseHeaders.getValue("x-secret-homework-header"), "Some secret value");

    }
}
