package cecs343.bpmontor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewMedHistory extends AppCompatActivity {
    SessionManager sesh;
    int patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_med_history);
        sesh = new SessionManager(getApplicationContext());
        patientId = sesh.getPid();

    }
}
