package cecs343.bpmontor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SessionManager sesh; // TODO: May need these for doctor implimentation
    int patientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();

        Button viewMedSched = findViewById(R.id.view_med_sched);
        Button viewMedHist =  findViewById(R.id.view_med_hist);
        Button viewBpHist =  findViewById(R.id.view_bp_hist);
        Button recordBp =  findViewById(R.id.record_bp);
        Button recordMeds =  findViewById(R.id.record_meds);

        Button logout = findViewById(R.id.logout);

        viewMedSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewMedSchedule.class);
                startActivity(i);
            }
        });

        viewMedHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewMedHistory.class);
                startActivity(i);
            }
        });

        viewBpHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewBPHistory.class);
                startActivity(i);
            }
        });

        recordBp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RecordBp.class);
                startActivity(i);
            }
        });

        recordMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RecordMed.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sesh.logoutUser();
            }
        });
    }
}
