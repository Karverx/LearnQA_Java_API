import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Task7LongRedirect {

    @Test
    public void testLongRedirect() {
        Response response = null;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode;
        int countRedirect = 0;

        while (true) {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

        statusCode = response.getStatusCode();

        if (statusCode == 200) break;
        countRedirect++;
        url = response.getHeader("Location");
        System.out.println(url);
    }
        System.out.println("Количество редиректов = " + countRedirect);
}
}
