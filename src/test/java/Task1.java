import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class Task1 {

    @Test
    public void testGetTask1(){
        Response response = RestAssured
                .get(" https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
}
