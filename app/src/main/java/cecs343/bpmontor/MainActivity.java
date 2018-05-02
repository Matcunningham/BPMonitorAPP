package cecs343.bpmontor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private SessionManager sesh;
    private static int patientId;
    private boolean isDoc;
    private int currentPatient;
    private String currPatientName;

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
            currPatientName = sesh.getCurrentPatName();
            if(currentPatient > 0) {
                setTitle("Current Patient: " + currPatientName);
            }
            else
            {
                setTitle("Select a patient");
            }
        }

        Button recordBp =  findViewById(R.id.record_bp);
        Button recordMeds =  findViewById(R.id.record_meds);
        Button viewMedSched = findViewById(R.id.view_med_sched);
        Button viewMedHist =  findViewById(R.id.view_med_hist);
        Button viewBpHist =  findViewById(R.id.view_bp_hist);
        Button updateSched = findViewById(R.id.update_sched);
        Button addDoc = findViewById(R.id.add_doc);

        Button logout = findViewById(R.id.logout);

        if(isDoc)
        {
            Button selPatient =  findViewById(R.id.sel_patient_btn);



            selPatient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), SelectPatient.class);
                    startActivity(i);
                }
            });


            if(currentPatient != patientId)
            {
                recordBp.setVisibility(View.INVISIBLE);
                recordMeds.setVisibility(View.INVISIBLE);
                addDoc.setVisibility(View.INVISIBLE);
            }
            if(currentPatient == -1)
            {
                viewMedHist.setVisibility(View.INVISIBLE);
                viewMedSched.setVisibility(View.INVISIBLE);
                viewBpHist.setVisibility(View.INVISIBLE);
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

        updateSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment upDiaFrag = new UpdateDialogFrag();
                FragmentManager fragMan = getFragmentManager();
                upDiaFrag.show(fragMan, "Update");

            }
        });

        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Dialog);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.add_doc_et, null);
                builder.setMessage("Enter your doctor's email so they can view your account information");
                builder.setTitle("ADD/UPDATE DOCTOR");
                builder.setView(layout);

                final EditText docEmail = layout.findViewById(R.id.email_doc);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String docMail = docEmail.getText().toString().trim();
                        new AddDocTask(getApplicationContext(), docMail).execute();
                    }
                });
                builder.show();
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


    public static class AddDocTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private String email;

        AddDocTask(Context ctx, String docMail)
        {
            context = ctx;
            email = docMail;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(AppConfig.URL_ADDDOC);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientId), "UTF-8")+"&"
                        +URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8") ;
                bfWriter.write(postData);
                bfWriter.flush();
                bfWriter.close();
                outStream.close();

                InputStream inStream = httpURLConnection.getInputStream();
                BufferedReader bfReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bfReader.readLine()) != null) {
                    result += line;
                }
                bfReader.close();
                inStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(final String result) {
            try {
                JSONObject jsonOb = new JSONObject(result);
                boolean status = jsonOb.getBoolean(AppConfig.SUCCESS);
                String message = jsonOb.getString("message");
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
