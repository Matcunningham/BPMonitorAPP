package cecs343.bpmontor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Mat on 5/4/2018.
 */
public class UserRegTaskTest extends RegisterActivity {

    String message;

    @Test // Test for registration, if user already exists
    public void test() {
        final UserRegTask userRegTask = new UserRegTask("abc@yahoo.com", "abcd", "Jack"){
            String result;

            @Override
            protected void onPostExecute(String result) {
                this.result = result;
            }

            public String toString()
            {
                return result;
            }
        };


        message = userRegTask.execute().toString();

        String expected = "{\"error\":true,\"error_msg\":\"User already exists\"}"; // Expected JSON String
        assertEquals(expected, message);

    }
    // Not including test for valid registration because it would only work one time
}