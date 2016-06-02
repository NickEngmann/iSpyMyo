package com.thalmic.android.iSpyMyo.iSpyMyo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.thalmic.android.sample.helloworld.R;
import com.thalmic.myo.scanner.ScanActivity;

public class HowTo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
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

    }
    public void savedtext(){

    }
    public void howtouse(){
        startActivity(new Intent("iSpyMyo.HowTo"));
    }
}
