package com.android.internal.telephony;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.varunrao.drivingtimecallhandler.MainActivity;
import com.example.varunrao.drivingtimecallhandler.R;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Created by varunrao on 09/02/17.
 */

public class BackgroundCallsRejecting extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    NotificationManager notificationManager;
    Vector<String> callersFromContactList=new Vector<String>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"All Calls to be rejected now.", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder notiBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("Driving Started")
                .setContentText("All Calls will be rejected")
                .setSmallIcon(R.drawable.driver_icon)
                .setOngoing(true);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,notiBuilder.build());
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        // register PhoneStateListener
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                //  React to incoming call.
                String number=incomingNumber;
                // If phone ringing
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    try
                    {
                        String name=findNameByNumber(incomingNumber);
                        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        Class clazz = Class.forName(telephonyManager.getClass().getName());
                        Method method = clazz.getDeclaredMethod("getITelephony");
                        method.setAccessible(true);
                        ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                        telephonyService.endCall();
                        if(name.equals("Not Found"))
                            Toast.makeText(getApplicationContext(),"Call From "+number+".Rejected.", Toast.LENGTH_SHORT).show();
                        else
                        {
                            callersFromContactList.add(name);
                            Toast.makeText(getApplicationContext(),"Call From "+name+".Rejected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Some Error:"+e, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    private String findNameByNumber(String incomingNumber) {
        String res = "Not Found";
        try {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
            Cursor c = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    res = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                c.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"Calls now available", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),"Callers who had called:", Toast.LENGTH_LONG).show();
        while(callersFromContactList.size()!=0)
        {
            String n=callersFromContactList.elementAt(0);
            Toast.makeText(getApplicationContext(),n, Toast.LENGTH_LONG).show();
            callersFromContactList.removeElementAt(0);
        }
        notificationManager.cancel(0);
        super.onDestroy();
    }
}
