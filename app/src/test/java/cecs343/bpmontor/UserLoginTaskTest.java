package cecs343.bpmontor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Mat on 5/5/2018.
 */
public class UserLoginTaskTest extends LoginActivity {

    String message;

    @Test // Test for invalid login
    public void testInvalid(){
        final UserLoginTask userLoginTask = new UserLoginTask("abc@yahoo.com", "abc"){
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


        message = userLoginTask.execute().toString();

        String expected = "{\"error\":true,\"error_msg\":\"Login credentials are wrong. Please try again!\"}"; // Expected JSON String
        assertEquals(expected, message);

    }

    @Test // Test for valid login
    public void testValid() {
        final UserLoginTask userLoginTask = new UserLoginTask("abc@yahoo.com", "abcd"){
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

        message = userLoginTask.execute().toString();

        String expected = "{\"error\":false,\"pid\":1,\"email\":\"abc@yahoo.com\"}"; // Expected JSON String
        assertEquals(expected, message);
    }
}