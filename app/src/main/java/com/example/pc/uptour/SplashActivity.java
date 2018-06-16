package com.example.pc.uptour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

/**
 * For the splash Screen
 */
public class SplashActivity extends AppCompatActivity
{

    private ProgressBar progressBar;

    InternetConnectivityCheck icc;

    private void ifconnected()
    {

        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {


                Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }

    private void ifnotConnected()
    {
        new AlertDialog.Builder(this).setIcon(R.drawable.error)
                .setTitle("Internet Connectivty is needed!")
                .setMessage("UPST requires internet connection to run properly.\n" +
                        "Please Turn on the Mobile data/Wifi to continue.")
                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (icc.haveNetworkConnection())
                            ifconnected();
                        else
                            ifnotConnected();
                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        icc=new InternetConnectivityCheck(SplashActivity.this);
        //hide the support ActionBar

        //noinspection ConstantConditions
        getSupportActionBar().hide();

        progressBar= findViewById(R.id.splashProgress);
        progressBar.setVisibility(View.INVISIBLE);

        if (icc.haveNetworkConnection())
            ifconnected();
        else
            ifnotConnected();

    }
}
