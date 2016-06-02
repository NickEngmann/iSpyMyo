package com.thalmic.android.iSpyMyo.iSpyMyo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.android.sample.helloworld.R;
import com.thalmic.myo.scanner.ScanActivity;

import java.io.IOException;

public class Save_Audio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save__audio);

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
        String[] saved_audio = new String[]{"audio_1.3gp","audio_2.3gp","audio_3.3gp","audio_4.3gp","audio_5.3gp","audio_6.3gp","audio_7.3gp","audio_8.3gp","audio_9.3gp","audio_10.3gp"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.da_item,
                saved_audio
        );

        //Configure the list view
        ListView list = (ListView) findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }
    private void registerClickCallBack() {
    ListView list = (ListView) findViewById(R.id.listView2);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
            TextView textView = (TextView) viewClicked;
            String message = " ";
            message = "Now playing " + textView.getText().toString();
            try {
                playrecording(textView.getText().toString());
                Toast.makeText(Save_Audio.this, message, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                message = "There is no file located here";
                Toast.makeText(Save_Audio.this, message, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    });
    }
    public void playrecording(String audiofile) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer mP = new MediaPlayer();
        mP.setDataSource(getFilesDir() + "/"+audiofile);
        mP.prepare();
        mP.start();
    }
}
