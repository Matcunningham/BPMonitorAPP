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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class UpdateSchedule extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MedChBoxRvAdapter mAdapter;
    private View mProgressView;
    private SessionManager sesh;
    private int selectedPatient;
    private String currPatientName;
    private boolean isDoc;
    private int patientId;
    private static String newTime;
    public List<String> medsSelected = new ArrayList<>(); // Holds checked medications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

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

        Button timePick = findViewById(R.id.time_select_update);
        Button updateSched = findViewById(R.id.update_sched_btn);

        mProgressView = findViewById(R.id.updatesched_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.update_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MedChBoxRvAdapter(R.layout.medcheckbox_list_item ,new MedChBoxRvAdapter.OnItemCheckListener() {
            // Overrides interface methods, adds/removes checked medication to array list to be updated
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

        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        if(isDoc) {
            new MedSchedQueryTask(selectedPatient).execute();
        }
        else
        {
            new MedSchedQueryTask(patientId).execute();
        }
        updateSched.setOnClickListener(new View.OnClickListener() {

            public void onClick(android.view.View view) {
                if(newTime == null || medsSelected.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All fields must be selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgressView.setVisibility(View.VISIBLE);
                    if (isDoc) {
                        new UpdateSchedTask(selectedPatient).execute();
                    } else {
                        new UpdateSchedTask(patientId).execute();
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

    // Displays time picker on button click
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default for the time picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Creates a new instance of TimePickerDialog and returns it
            return new TimePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        // Transforms the time into a string readable by mySQL
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
            newTime = hr + min;
        }
    }

    public class UpdateSchedTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private ArrayList<String> jsonList = new ArrayList<>();
        private int id;

        UpdateSchedTask(int pid)
        {
            id = pid;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                // HTTP POST Request, returns JSON String for parsing, JSON strings added to array in case of multiple updates.
                for(int i = 0; i < medsSelected.size(); i++) {
                    // Parsing strings of selected meds to get the name and time
                    String oldTime = "";
                    String med = "";
                    StringTokenizer st = new StringTokenizer(medsSelected.get(i), "\t\t\t");
                    while (st.hasMoreTokens()) {
                        med = st.nextToken();
                        oldTime = st.nextToken();
                    }
                    URL url = new URL(AppConfig.URL_UPDATESCHED);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outStream = httpURLConnection.getOutputStream();
                    BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                    String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8") + "&"
                            + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(med, "UTF-8") + "&"
                            + URLEncoder.encode("oldtime", "UTF-8") + "=" + URLEncoder.encode(oldTime, "UTF-8") + "&"
                            + URLEncoder.encode("newtime", "UTF-8") + "=" + URLEncoder.encode(newTime, "UTF-8");
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
            mProgressView.setVisibility(View.GONE);
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
                    Intent i = new Intent(getApplicationContext(), ViewMedSchedule.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public class MedSchedQueryTask extends AsyncTask<Void, Void, String> {

        private int id;

        MedSchedQueryTask(int pid)
        {
            id = pid;
        }

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
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8");
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
            mProgressView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
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
                        String time = row.getString(AppConfig.timeTag);

                        String lineFormat = drugName + "\t\t\t" + time;
                        data[i] = lineFormat;

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
