package com.uniprogy.outquiz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.uniprogy.outquiz.activities.home.HomeActivity;
import com.uniprogy.outquiz.activities.signin.LoginActivity;
import com.uniprogy.outquiz.helpers.Misc;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("close", false)){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finishAffinity();
            return;
        }

        if(Misc.getCurrentPlayer() != null)
        {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
