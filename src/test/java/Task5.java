import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class Task5 {

    @Test
    public void testGetSecondMessage(){
        JsonPath response = RestAssured
                .get(" https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        ArrayList second = response.get("messages");
        System.out.println(second.get(1));
    }
}
