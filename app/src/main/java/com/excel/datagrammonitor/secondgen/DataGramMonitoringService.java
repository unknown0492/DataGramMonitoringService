package com.excel.datagrammonitor.secondgen;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.Constants;
import com.excel.excelclasslibrary.RetryCounter;
import com.excel.excelclasslibrary.UtilMisc;

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

    @Override
    public void onCreate() {
        super.onCreate();

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(this, "test" );
        notificationBuilder.setSmallIcon( R.drawable.ic_launcher );
        notificationManager.notify(0, notificationBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( "test",TAG, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel( channel );

            Notification notification = new Notification.Builder(getApplicationContext(),"test").build();
            startForeground(1, notification);
        }
        else {
            // startForeground(1, notification);
        }
    }

    public void scheduleMonitorAlarm( Context context ){

        Log.d( TAG, "scheduleMonitorAlarm()" );

        AlarmManager am1 =(AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        Intent in1 = new Intent( context, Receiver.class );
        in1.setAction( "monitor_listening_service" );
        PendingIntent pi1 = PendingIntent.getBroadcast( context, 0, in1, 0 );

        cal = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        long afterFiveMinutes = currentTime + 30000;//FIVE_MINUTES_MILLIS;
        cal.setTimeInMillis( afterFiveMinutes );

        am1.set( AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi1 );

        /*Intent in = new Intent( "start_listening_service" );
        sendBroadcast( in );*/
        UtilMisc.sendExplicitExternalBroadcast( context, "start_listening_service", Constants.REMOTELYCONTROL_PACKAGE_NAME, Constants.REMOTELYCONTROL_RECEIVER_NAME );

        Log.d( TAG,  "Alarm Day : "+cal.get( Calendar.DATE ) );
        Log.d( TAG,  "Alarm Month : "+cal.get( Calendar.MONTH ) + 1 );
        Log.d( TAG,  "Alarm Year : "+cal.get( Calendar.YEAR ) );
        Log.d( TAG,  "Alarm Hours : "+cal.get( Calendar.HOUR_OF_DAY ) );
        Log.d( TAG,  "Alarm Minutes : "+cal.get( Calendar.MINUTE ) );
        Log.d( TAG,  "Alarm Seconds : "+cal.get( Calendar.SECOND ) );
    }

}
