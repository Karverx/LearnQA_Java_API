package lib;

import java.text.SimpleDateFormat;


public class DataGenerator {
    public static String getRandomEmail() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timeStamp + "@example.com";
    }
}
