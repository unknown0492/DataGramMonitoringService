package com.excel.datagrammonitor.secondgen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.RetryCounter;

import java.util.Calendar;

public class DataGramMonitoringService extends Service {
    Context context = this;
    final static String TAG = "MonitoringService";
    RetryCounter retryCounter;
    Calendar cal;
    int alarm_hours, alarm_minutes;
    boolean skip = false;
    ConfigurationReader configurationReader = null;
    static long FIVE_MINUTES_MILLIS = 5 * 60 * 1000;


    @Override
    public IBinder onBind(Intent intent ) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        Log.i( TAG, "DataGramMonitoringService started" );

        scheduleMonitorAlarm( context );

        return START_NOT_STICKY;
    }

    public void scheduleMonitorAlarm( Context context ){

        Log.d( TAG, "scheduleMonitorAlarm()" );

        AlarmManager am1 =(AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        Intent in1 = new Intent( "monitor_listening_service" );
        PendingIntent pi1 = PendingIntent.getBroadcast( context, 0, in1, 0 );

        cal = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        long afterFiveMinutes = currentTime + FIVE_MINUTES_MILLIS;
        cal.setTimeInMillis( afterFiveMinutes );

        am1.set( AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi1 );

        Intent in = new Intent( "start_listening_service" );
        sendBroadcast( in );

        Log.d( TAG,  "Alarm Day : "+cal.get( Calendar.DATE ) );
        Log.d( TAG,  "Alarm Month : "+cal.get( Calendar.MONTH ) + 1 );
        Log.d( TAG,  "Alarm Year : "+cal.get( Calendar.YEAR ) );
        Log.d( TAG,  "Alarm Hours : "+cal.get( Calendar.HOUR_OF_DAY ) );
        Log.d( TAG,  "Alarm Minutes : "+cal.get( Calendar.MINUTE ) );
        Log.d( TAG,  "Alarm Seconds : "+cal.get( Calendar.SECOND ) );
    }

}
