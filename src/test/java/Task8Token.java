import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class Task8Token {
    @Test
    public void testToken() throws InterruptedException {
        String token, status, result;
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        int seconds;
        Map<String, String> data = new HashMap<>();

        JsonPath response = RestAssured
                .get(url)
                .jsonPath();

        response.prettyPrint();
        token = response.get("token");
        seconds = response.get("seconds");
        data.put("token", token);

        response = RestAssured
                .given()
                .queryParams(data)
                .when()
                .get(url)
                .jsonPath();

        response.prettyPrint();
        status = response.get("status");

        assertEquals("Job is NOT ready", status);

        Thread.sleep(1000 * seconds);

        response = RestAssured
                .given()
                .queryParams(data)
                .when()
                .get(url)
                .jsonPath();

        response.prettyPrint();
        status = response.get("status");
        result = response.get("result");

        assertEquals("Job is ready", status);
        assertNotNull(result);

        System.out.println("Ответ на «главный вопрос Жизни, Вселенной и Всего Остального»:" + result);
    }
}
