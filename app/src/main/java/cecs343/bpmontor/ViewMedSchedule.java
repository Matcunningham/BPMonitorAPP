package cecs343.bpmontor;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
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

public class ViewMedSchedule extends AppCompatActivity {

    // Session managment and UI references
    private RecyclerView mRecyclerView;
    private BpRvAdapter mAdapter;
    private SessionManager sesh;
    private int patientId;
    private boolean isDoc;
    private String currPatientName;
    private int selectedPatient;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_med_schedule);
        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin(); // Redirects to login page if not logged in
        patientId = sesh.getPid(); // Gets patient ID
        isDoc = sesh.getIsDoc(); // Sets boolean
        if(isDoc) // If doctor get the current selected patient
        {
            selectedPatient = sesh.getCurrentPat();
            currPatientName = sesh.getCurrentPatName();
            setTitle("Current Patient: " + currPatientName);
        }

        mProgressView = findViewById(R.id.medsched_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.medsched_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new BpRvAdapter(R.layout.bp_list_item);
        mRecyclerView.setAdapter(mAdapter);

        // Setting progress spinner to visible
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        if(isDoc)
        {
            new MedSchedQueryTask(selectedPatient).execute();
        }
        else
        {
            new MedSchedQueryTask(patientId).execute();
        }
    }

    // For upward navigation, returns back to parent activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Asynchronous task to retrive medication schedule
    public class MedSchedQueryTask extends AsyncTask<Void, Void, String> {

        private final int id;

        MedSchedQueryTask(int pid)
        {
            id = pid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // HTTP POST Request, returns JSON String for parsing.
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

                // Getting JSON
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
            // Removes progress spinner and replaces with recycler view
            mProgressView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            try {
                // Parsing json
                JSONObject jsonOb = new JSONObject(result);
                boolean status = jsonOb.getBoolean(AppConfig.SUCCESS);
                if(status)
                {
                    JSONArray json = jsonOb.getJSONArray("data");
                    String[] data = new String[json.length()]; // Holds each row of medication
                    for(int i = 0; i < json.length(); i++)
                    {
                        JSONObject row = json.getJSONObject(i);
                        String drugName =  row.getString(AppConfig.mednameTag);
                        String time = row.getString(AppConfig.timeTag);
                        time = time.substring(0, time.length()-3);

                        String lineFormat = String.format("%-50s %-6s",drugName,time);
                        data[i] = lineFormat;

                    }
                    mAdapter.setBpData(data); // Setting list item for recycler view row

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

