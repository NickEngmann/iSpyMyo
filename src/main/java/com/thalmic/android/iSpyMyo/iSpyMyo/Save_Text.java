package com.thalmic.android.iSpyMyo.iSpyMyo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.android.sample.helloworld.R;
import com.thalmic.myo.scanner.ScanActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Save_Text extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save__text);
        populateListView();
        registerClickCallBack();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        if (R.id.home == id) {
            returnhome();
        }
        if (R.id.saved_audio == id) {
            savedaudio();
        }
        if (R.id.saved_text == id) {
            savedtext();
        }
        if (R.id.how_to_use == id) {
            howtouse();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
    public void returnhome(){
        Intent intent = new Intent(this, Main_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void savedaudio(){
        startActivity(new Intent("iSpyMyo.Save_Audio"));
    }
    public void savedtext(){
        startActivity(new Intent("iSpyMyo.Save_Text"));
    }
    public void howtouse(){
        startActivity(new Intent("iSpyMyo.HowTo"));
    }
    private void populateListView() {
        //Create list of items
        String[] saved_text = new String[]{"text_1.txt","text_2.txt","text_3.txt","text_4.txt","text_5.txt","text_6.txt","text_7.txt","text_8.txt","text_9.txt","text_10.txt"};        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.da_item,
                saved_text
        );

        //Configure the list view
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }
    private void registerClickCallBack() {
        ListView list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String message = " ";
                message = readFromFile(textView.getText().toString(), message);
                Toast.makeText(Save_Text.this, textView.getText().toString() + ": " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private String readFromFile(String location, String message) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(location);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            message = "There is no file located here";
            Toast.makeText(Save_Text.this, message, Toast.LENGTH_LONG).show();
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            message = "There is no file located here";
            Toast.makeText(Save_Text.this, message, Toast.LENGTH_LONG).show();
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
