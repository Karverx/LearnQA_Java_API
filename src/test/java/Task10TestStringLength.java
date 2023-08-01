import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class Task10TestStringLength {

    @ParameterizedTest
    @ValueSource(strings = {"", "1234", "15 lengthstring", "16 length string", "TestString for 24 length"})
    public void testStringLengthGreaterThan15(String str) {

        assertThat(str.length(), greaterThan(15));

    }
}
