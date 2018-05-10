package cecs343.bpmontor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class RecordMed extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MedChBoxRvAdapter mAdapter;
    private SessionManager sesh;
    private int patientId;
    public List<String> medsSelected = new ArrayList<>();
    private static String date;
    private static String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_med);
        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();

        Button datePick = findViewById(R.id.date_select_recmed);
        Button timePick = findViewById(R.id.time_select_recmed);
        Button recordMed = findViewById(R.id.record_med_btn);

        mRecyclerView = (RecyclerView) findViewById(R.id.medcheckbox_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MedChBoxRvAdapter(R.layout.medcheckbox_list_item, new MedChBoxRvAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(String med) {
                medsSelected.add(med);
            }

            @Override
            public void onItemUncheck(String med) {
                medsSelected.remove(med);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        new MedSchedQueryTask().execute();

        recordMed.setOnClickListener(new View.OnClickListener() {

            public void onClick(android.view.View view) {
                if(medsSelected.isEmpty() || date == null || time == null)
                {
                    Toast.makeText(getApplicationContext(), "All fields must be selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RecordMedTask(patientId).execute();
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
            // Use the current date as the default
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_DARK, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
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
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
            time = hr + min;
        }
    }

    public class RecordMedTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private final int id;
        private ArrayList<String> jsonList = new ArrayList<>();

        RecordMedTask(int pid)
        {
            id = pid;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {


            try {
                for(int i = 0; i < medsSelected.size(); i++) {
                    URL url = new URL(AppConfig.URL_RECMED);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outStream = httpURLConnection.getOutputStream();
                    BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                    String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8") + "&"
                            + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(medsSelected.get(i), "UTF-8") + "&"
                            + URLEncoder.encode("dte", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&"
                            + URLEncoder.encode("tim", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                    bfWriter.write(postData);
                    bfWriter.flush();
                    bfWriter.close();
                    outStream.close();

                    InputStream inStream = httpURLConnection.getInputStream();
                    BufferedReader bfReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"));
                    String line = "";
                    String result = "";
                    while ((line = bfReader.readLine()) != null) {

                        result += line;
                    }

                    bfReader.close();
                    inStream.close();
                    httpURLConnection.disconnect();
                    jsonList.add(result);
                }

                return jsonList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ArrayList<String>();
        }

        @Override
        protected void onPostExecute(final ArrayList<String> result) {
            try {
                boolean flag = true;
                for(int i = 0; i < result.size(); i++) {

                    JSONObject json = new JSONObject(result.get(i));
                    Boolean errorStatus = json.getBoolean("success");
                    String message = json.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if(errorStatus == false)
                    {
                        flag = false;
                    }
                }
                if(flag != false) {
                    Intent i = new Intent(getApplicationContext(), ViewMedHistory.class);
                    startActivity(i);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public class MedSchedQueryTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(AppConfig.URL_MEDSCHED);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientId), "UTF-8");
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
                if(status)
                {
                    JSONArray json = jsonOb.getJSONArray("data");
                    String[] data = new String[json.length()];
                    for(int i = 0; i < json.length(); i++)
                    {
                        JSONObject row = json.getJSONObject(i);
                        String drugName =  row.getString(AppConfig.mednameTag);

                        String line = drugName;
                        data[i] = line;

                    }
                    mAdapter.setBpData(data);

                }
                else {
                    String message = jsonOb.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
