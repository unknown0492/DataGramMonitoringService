package com.excel.datagrammonitor.secondgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.UtilShell;

public class Receiver extends BroadcastReceiver {

    final static String TAG = "Receiver";
    ConfigurationReader configurationReader;
    SharedPreferences spfs;

    @Override
    public void onReceive(Context context, Intent intent ) {
        String action = intent.getAction();
        Log.d( TAG, "action : " + action );

        configurationReader = ConfigurationReader.reInstantiate();

        if( action.equals( "android.net.conn.CONNECTIVITY_CHANGE" ) || action.equals( "connectivity_changed" ) ){

            // 1. First time in order to receive broadcasts, the app should be started at least once
            startDataGramActivity( context );

            // Execute these broadcasts only when the OTS IS COMPLETED
            String is_ots_completed = configurationReader.getIsOtsCompleted().trim();
            if( is_ots_completed.equals( "0" ) ) {
                Log.d( TAG, "OTS has not been completed, DataGramMonitoringService Broadcasts will not execute !" );
                return;
            }

            if( ! isConnectivityBroadcastFired() ) {

                // 2. Start UDP Listening service
                context.sendBroadcast( new Intent( "monitor_listening_service" ) );

                setConnectivityBroadcastFired( true );
            }

        }
        else if( action.equals( "android.intent.action.BOOT_COMPLETED" ) || action.equals( "boot_completed" ) ){
            context.sendBroadcast( new Intent( "monitor_listening_service" ) );
        }
        else if( action.equals( "monitor_listening_service" ) ){
            startDataGramMonitoringService( context );
        }

    }

    private void startDataGramActivity( Context context ){
        // Start this app activity
        Intent in = new Intent( context, MainActivity.class );
        in.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( in );
    }

    private void startDataGramMonitoringService( Context context ){
        Intent in = new Intent( context, DataGramMonitoringService.class );
        context.startService( in );
    }



    private void setConnectivityBroadcastFired( boolean is_it ){
        String s = (is_it)?"1":"0";
        Log.d( TAG, "setConnectivityBroadcastFired() : " + s );
        UtilShell.executeShellCommandWithOp( "setprop dg_br_fired " + s );
    }

    private boolean isConnectivityBroadcastFired(){
        String is_it = UtilShell.executeShellCommandWithOp( "getprop dg_br_fired" ).trim();
        return ( is_it.equals( "0" ) || is_it.equals( "" ) )?false:true;
    }

}
