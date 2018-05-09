package com.excel.datagrammonitor.secondgen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    static final String TAG = "DataGramMonitor";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Log.d( TAG, "DataGramMonitor MainActivity() started" );

        finish();

    }
}
