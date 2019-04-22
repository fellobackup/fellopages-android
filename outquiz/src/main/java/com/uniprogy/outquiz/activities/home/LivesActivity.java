package com.uniprogy.outquiz.activities.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;

public class LivesActivity extends AppCompatActivity {

    TextView infoTextView;
    Button codeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lives);
        configureView();
    }

    private void configureView()
    {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        infoTextView = findViewById(R.id.infoTextView);
        codeButton = findViewById(R.id.codeButton);
        codeButton.addOnLayoutChangeListener(new RoundedCornersListener());
        codeButton.setText(Misc.getCurrentPlayer().referral);
        codeButtonTap();
    }

    private void codeButtonTap()
    {
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuShare();
            }
        });
    }

    private void showMenuShare()
    {
        String shareText = getString(R.string.tr_share_text,
                getString(R.string.app_name),
                Misc.getCurrentPlayer().referral,
                getString(R.string.app_host));

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
