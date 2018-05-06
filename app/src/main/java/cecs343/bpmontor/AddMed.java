package cecs343.bpmontor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;

public class AddMed extends AppCompatActivity {

    private SessionManager sesh;
    private int selectedPatient;
    private String currPatientName;
    private boolean isDoc;
    private int patientId;
    private String newMed;
    private static String medTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();
        isDoc = sesh.getIsDoc();
        if(isDoc)
        {
            selectedPatient = sesh.getCurrentPat();
            currPatientName = sesh.getCurrentPatName();
            setTitle("Current Patient: " + currPatientName);
        }

        final EditText etMed = findViewById(R.id.etnew_med);
        Button timePick = findViewById(R.id.time_select_newmed);
        Button addMed = findViewById(R.id.record_newmed_btn);

        addMed.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                newMed = etMed.getText().toString().trim();
                if(TextUtils.isEmpty(newMed))
                {
                    Toast.makeText(getApplicationContext(), "You Must Enter a name", Toast.LENGTH_LONG).show();
                }
                else {
                    if (medTime == null) {
                        Toast.makeText(getApplicationContext(), "You Must Choose a Time", Toast.LENGTH_LONG).show();
                    } else {
                        if (isDoc) {
                            new InsertMedTask(selectedPatient).execute();
                        } else {
                            new InsertMedTask(patientId).execute();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "timePickerAdd");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(hourOfDay < 5 && hourOfDay > 0)
            {
                Toast.makeText(getActivity(), "WARNING: Time is set during sleep hours", Toast.LENGTH_LONG).show();
            }
            String hr;
            String min;
            if (hourOfDay < 10) {
                hr = "0" + String.valueOf(hourOfDay) + ":";
            } else {
                hr = String.valueOf(hourOfDay) + ":";
            }
            if (minute < 10) {
                min = "0" + String.valueOf(minute);
            } else {
                min = String.valueOf(minute);
            }
            medTime = hr + min;

        }
    }

    public class InsertMedTask extends AsyncTask<Void, Void, String> {

        private int id;

        InsertMedTask(int pid)
        {
            id = pid;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(AppConfig.URL_ADDMED);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8") + "&"
                        + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(newMed, "UTF-8") + "&"
                        + URLEncoder.encode("tim", "UTF-8") + "=" + URLEncoder.encode(medTime, "UTF-8");
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
                JSONObject json = new JSONObject(result);
                Boolean errorStatus = json.getBoolean("success");
                String message = json.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                if (errorStatus) {
                    Intent i = new Intent(getApplicationContext(), ViewMedSchedule.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
