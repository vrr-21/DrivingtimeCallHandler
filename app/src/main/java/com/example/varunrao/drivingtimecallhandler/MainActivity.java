package com.example.varunrao.drivingtimecallhandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
