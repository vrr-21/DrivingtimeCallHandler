package com.example.varunrao.drivingtimecallhandler;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.BackgroundCallsRejecting;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] perms = {Manifest.permission.READ_CONTACTS,
                          Manifest.permission.READ_PHONE_STATE,
                          Manifest.permission.CALL_PHONE,
                          Manifest.permission.MODIFY_PHONE_STATE,
                          Manifest.permission.SEND_SMS};

        int permsRequestCode = 200;

        ActivityCompat.requestPermissions(this,perms, permsRequestCode);

        //Permission Asking Code: Before one
        /*if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.MODIFY_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MODIFY_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MODIFY_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }*/

        SharedPreferences getCurrentMode = getApplicationContext().getSharedPreferences("currentState", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getCurrentMode.edit();

        final String checkFirstRunKey="isFirstRun";
        final String isCurrentDrivingKey="isCurrentDriving";
        if(getCurrentMode.getBoolean(checkFirstRunKey,true))
        {
            //Toast.makeText(getApplicationContext(),"Welcome for the first time!",Toast.LENGTH_SHORT).show();
            editor.putBoolean(isCurrentDrivingKey,false).commit();
            editor.putBoolean(checkFirstRunKey,false).commit();
        }

        final Button startDrive=(Button)findViewById(R.id.drivingStart);
        final Button endDrive=(Button)findViewById(R.id.drivingOver);
        final TextView someText=(TextView)findViewById(R.id.someText);

        if(getCurrentMode.getBoolean(isCurrentDrivingKey, Boolean.parseBoolean(null)))
        {
            startDrive.setVisibility(View.INVISIBLE);
            endDrive.setVisibility(View.VISIBLE);
            someText.setText("All Calls will be rejected automatically, and for your contacts a message will be sent. Concentrate on the driving and not the phone!");
        }
        else
        {
            endDrive.setVisibility(View.INVISIBLE);
            startDrive.setVisibility(View.VISIBLE);
            someText.setText("This app will automatically reject any incoming calls coming after you've pressed the above button. If the call has come from a contact of yours, then that person will be sent a message that you are currently driving and hence cannot attend the call.");
        }
        startDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDrive.setVisibility(View.INVISIBLE);
                endDrive.setVisibility(View.VISIBLE);
                someText.setText("All Calls will be rejected automatically, and for your contacts a message will be sent. Concentrate on the driving and not the phone!");
                editor.putBoolean(isCurrentDrivingKey,true).commit();
                startService(new Intent(getBaseContext(), BackgroundCallsRejecting.class));

            }
        });

        endDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDrive.setVisibility(View.INVISIBLE);
                startDrive.setVisibility(View.VISIBLE);
                someText.setText("This app will automatically reject any incoming calls coming after you've pressed the above button. If the call has come from a contact of yours, then that person will be sent a message that you are currently driving and hence cannot attend the call.");
                editor.putBoolean(isCurrentDrivingKey,false).commit();
                stopService(new Intent(getBaseContext(),BackgroundCallsRejecting.class));
            }
        });
    }

    /*public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),"Thank You!",Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(),"WHY!",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }*/

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        //Toast.makeText(getApplicationContext(),grantResults.length,Toast.LENGTH_LONG).show();

        switch(permsRequestCode){
            case 200:{

                if (grantResults.length>0&&checkIfAllGranted(grantResults,permissions.length-1))//grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED&&grantResults[2]==PackageManager.PERMISSION_GRANTED&&grantResults[3]==PackageManager.PERMISSION_GRANTED) {
                {
                    Toast.makeText(getApplicationContext(),"Thank You!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"WHY!",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private boolean checkIfAllGranted(int[] grantResults,int length) {
        for (int i=0;i<length;i++)
        {
            if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}
