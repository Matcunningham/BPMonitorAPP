package cecs343.bpmontor;

import org.json.JSONException;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Mat on 5/5/2018.
 */
public class UserLoginTaskTest extends LoginActivity {

    String message;

    // Test for invalid login
    @Test
    public void testInvalid() throws ExecutionException, InterruptedException, JSONException {
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

    // Test for valid login
    @Test
    public void testValid() throws ExecutionException, InterruptedException, JSONException {
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