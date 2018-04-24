package cecs343.bpmontor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

public class RecordBp extends AppCompatActivity {
    private SessionManager sesh;
    private int patientId;
    private static String date;
    private static String time;
    private String sys;
    private String dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_bp);
        sesh = new SessionManager(getApplicationContext());
        patientId = sesh.getPid();

        Button datePick = findViewById(R.id.date_select);
        Button timePick = findViewById(R.id.time_select);
        final EditText etSys = findViewById(R.id.sys);
        final EditText etDia = findViewById(R.id.dia);
        Button record = findViewById(R.id.record_bp_in_db);

        // etSys.setInputType(InputType.TYPE_CLASS_NUMBER);
        //etDia.setInputType(InputType.TYPE_CLASS_NUMBER);

        record.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                sys = etSys.getText().toString().trim();
                dia = etDia.getText().toString().trim();
                new InsertBpTask().execute();
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_DARK, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month + 1;
            String mth;
            String dy;
            if (month < 10) {
                mth = "-0" + String.valueOf(month) + "-";
            } else {
                mth = "-" + String.valueOf(month) + "-";
            }
            if (day < 10) {
                dy = "0" + String.valueOf(day);
            } else {
                dy = String.valueOf(day);
            }

            date = String.valueOf(year) + mth + dy;
            //Toast.makeText(getActivity(), date, Toast.LENGTH_LONG).show();
        }
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
            // Do something with the time chosen by the user
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
            time = hr + min + ":00";
            Toast.makeText(getActivity(), time, Toast.LENGTH_LONG).show();
        }
    }

    public class InsertBpTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                URL url = new URL(AppConfig.URL_RECBP);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientId), "UTF-8") + "&"
                        + URLEncoder.encode("dte", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&"
                        + URLEncoder.encode("tim", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&"
                        + URLEncoder.encode("sys", "UTF-8") + "=" + URLEncoder.encode(sys, "UTF-8") + "&"
                        + URLEncoder.encode("dia", "UTF-8") + "=" + URLEncoder.encode(dia, "UTF-8");
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
                    Intent i = new Intent(getApplicationContext(), ViewBPHistory.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
