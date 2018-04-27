package cecs343.bpmontor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private SessionManager sesh;
    private int patientId;
    private boolean isDoc;
    private int currentPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();
        isDoc = sesh.getIsDoc();

        if(!isDoc)
        {
            setContentView(R.layout.activity_main);
        }
        else
        {
            setContentView(R.layout.activity_main_doctor);
            currentPatient = sesh.getCurrentPat();
        }

        Button recordBp =  findViewById(R.id.record_bp);
        Button recordMeds =  findViewById(R.id.record_meds);
        Button viewMedSched = findViewById(R.id.view_med_sched);
        Button viewMedHist =  findViewById(R.id.view_med_hist);
        Button viewBpHist =  findViewById(R.id.view_bp_hist);
        Button logout = findViewById(R.id.logout);

        if(isDoc)
        {
            Button selPatient =  findViewById(R.id.sel_patient_btn);
            Button updateSched = findViewById(R.id.update_sched);


            selPatient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), SelectPatient.class);
                    startActivity(i);
                }
            });

            updateSched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment upDiaFrag = new UpdateDialogFrag();
                    FragmentManager fragMan = getFragmentManager();
                    upDiaFrag.show(fragMan, "Update");

                }
            });

            if(currentPatient != patientId)
            {
                recordBp.setVisibility(View.INVISIBLE);
                recordMeds.setVisibility(View.INVISIBLE);
            }
            if(currentPatient == -1)
            {
                viewMedHist.setVisibility(View.INVISIBLE);
                viewMedSched.setVisibility(View.INVISIBLE);
                viewBpHist.setVisibility(View.INVISIBLE);
                updateSched.setVisibility(View.INVISIBLE);
            }
            if(currentPatient == patientId)
            {
                updateSched.setVisibility(View.INVISIBLE);
            }
        }


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



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sesh.logoutUser();
                finish();
            }
        });
    }

    public static class UpdateDialogFrag extends DialogFragment {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Would you like to...\nUpdate time for an extisting med\nAdd a new med\nDelete a med");
            builder.setTitle("Alter Medication");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(), UpdateSchedule.class);
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(), DeleteMeds.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(), AddMed.class);
                    startActivity(intent);
                }
            });
            return builder.create();
        }
    }
}
