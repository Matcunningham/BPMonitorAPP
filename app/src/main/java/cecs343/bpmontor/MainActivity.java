package cecs343.bpmontor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SessionManager sesh;
    int patientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sesh = new SessionManager(getApplicationContext());
        patientId = sesh.getPid();

        Button viewMedSched = findViewById(R.id.view_med_sched);
        Button viewMedHist =  findViewById(R.id.view_med_hist);
        Button viewBpHist =  findViewById(R.id.view_bp_hist);

        viewMedSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewMedSchedule.class);
                startActivity(i);
                finish();
            }
        });

        viewMedHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewMedHistory.class);
                startActivity(i);
                finish();
            }
        });

        viewBpHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewBPHistory.class);
                startActivity(i);
                finish();
            }
        });
    }
}
