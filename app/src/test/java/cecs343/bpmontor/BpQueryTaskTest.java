package cecs343.bpmontor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Mat on 5/7/2018.
 */
public class BpQueryTaskTest extends ViewBPHistory {
    String message;

    @Test // Test for user that has no blood pressure recorded
    public void test() {
        final BpQueryTask bpQueryTask = new BpQueryTask(0){
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

        message = bpQueryTask.execute().toString();

        String expected = "{\"success\":false,\"message\":\"No Entries Found\"}"; // Expected JSON String
        assertEquals(expected, message);
    }
    // Test for user with entries not included, because data will change
}