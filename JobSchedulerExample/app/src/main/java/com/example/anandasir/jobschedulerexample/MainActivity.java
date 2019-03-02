package com.example.anandasir.jobschedulerexample;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ListView listView;
    TextView textView;
    MyAdapter adapter;

    ArrayList<Item> urlList = new ArrayList<Item>() {{
        add(new Item("https://www.google.co.in", "Not Yet Started"));
        add(new Item("https://www.facebook.com", "Not Yet Started"));
        add(new Item("https://www.irctc.co.in", "Not Yet Started"));
        add(new Item("https://www.youtube.com", "Not Yet Started"));
        add(new Item("https://www.wikipedia.org/", "Not Yet Started"));
        add(new Item("https://in.yahoo.com/?p=us", "Not Yet Started"));
        add(new Item("https://www.amazon.com/", "Not Yet Started"));
        add(new Item("https://twitter.com/", "Not Yet Started"));
        add(new Item("https://www.instagram.com/", "Not Yet Started"));
        add(new Item("https://www.netflix.com/in/", "Not Yet Started"));
        add(new Item("https://www.linkedin.com/", "Not Yet Started"));
        add(new Item("https://www.whatsapp.com/", "Not Yet Started"));
        add(new Item("https://www.microsoft.com/en-in/", "Not Yet Started"));
        add(new Item("https://www.etest.com", "Not Yet Started"));
        add(new Item("http://ivymobility.com/", "Not Yet Started"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.txtConnectionStatus);
        listView = (ListView) findViewById(R.id.listView);
        urlList = (getConnectedList() == null) ? urlList : getList(getConnectedList());
        adapter = new MyAdapter(this, R.layout.row, urlList);
        listView.setAdapter(adapter);
        clear();
    }

    public void scheduleJob() {
        ComponentName name = new ComponentName(this, SimpleService.class);
        JobInfo info = new JobInfo.Builder(123, name)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .setPersisted(true).build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int response = scheduler.schedule(info);
        if (response == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job Scheduled");
        } else {
            Log.d(TAG, "Job not Scheduled");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        ArrayList<Integer> list = getConnectedList();
        urlList = getList(list);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Job Cancelled");
    }

    public ArrayList<Item> getList(ArrayList<Integer> list) {
        for (int i = 0; i < urlList.size(); i++) {
            if (list.contains(i))
                urlList.get(i).setTxtStatus("Passed");
            else
                urlList.get(i).setTxtStatus("Failed");
        }
        return urlList;
    }

    public ArrayList<Integer> getConnectedList() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("Store", "");
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void clear(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startJob:
                scheduleJob();
                System.exit(0);
                break;
            case R.id.cancelJob:
                cancelJob();
                break;
        }
        return true;
    }

    public class MyAdapter extends ArrayAdapter<Item> {

        ArrayList<Item> urlList = new ArrayList<>();

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
            super(context, textViewResourceId, objects);
            urlList = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row, null);
            TextView textURL = (TextView) v.findViewById(R.id.txtURL);
            TextView textStatus = (TextView) v.findViewById(R.id.txtStatus);
            textURL.setText(urlList.get(position).getTxtURL());
            textStatus.setText(urlList.get(position).getTxtStatus());
            return v;

        }

    }
}
