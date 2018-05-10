package cecs343.bpmontor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

public class SelectPatient extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RadioRecyclerAdapter mAdapter;
    private View mProgressView;
    private int patientId;
    private String patName;
    private SessionManager sesh;
    private int currentPatient = -1;
    private Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patient);
        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();

        mProgressView = findViewById(R.id.selpatient_progress);
        Button selPatient = (Button) findViewById(R.id.select_patient);
        mRecyclerView = (RecyclerView) findViewById(R.id.sel_patient_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RadioRecyclerAdapter(new RadioRecyclerAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(int patient, String name) {
                currentPatient = patient;
                patName = name;
            }

        });
        mRecyclerView.setAdapter(mAdapter);

        retry = (Button) findViewById(R.id.retry_selpatient);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        new SelectPatientTask().execute();

        selPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPatient != -1) {
                    sesh.setCurrentPat(currentPatient);
                    sesh.setCurrentPatName(patName);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You must select a patient", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                new SelectPatientTask().execute();
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

    public class SelectPatientTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(AppConfig.URL_SELPAT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(AppConfig.HTTP_TIME_OUT);

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
                httpURLConnection.setReadTimeout(AppConfig.HTTP_TIME_OUT);
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
            }
            catch(java.net.SocketTimeoutException tOut)
            {
                this.cancel(true);
            }
            catch (IOException e) {
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
                    List<Integer> pidData = new ArrayList<>();
                    List<String> nameData = new ArrayList<>();
                    for(int i = 0; i < json.length(); i++)
                    {
                        JSONObject row = json.getJSONObject(i);
                        data[i] = row.getString(AppConfig.emailTag);
                        pidData.add(row.getInt(AppConfig.pidTag));
                        nameData.add(row.getString(AppConfig.FULL_NAME));
                    }
                    mAdapter.setPatData(data, pidData, nameData);
                }
                else {
                    String message = jsonOb.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressView.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Time Out... TRY AGAIN", Toast.LENGTH_LONG).show();
        }
    }
}
