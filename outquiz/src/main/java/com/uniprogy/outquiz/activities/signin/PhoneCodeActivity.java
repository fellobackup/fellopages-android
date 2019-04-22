package com.uniprogy.outquiz.activities.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.MainActivity;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.helpers.RoundedCornersStrokeListener;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONObject;

public class PhoneCodeActivity extends BaseActivity {

    private String phone;
    private String country;
    private String number;

    private int defaultResendTime = 30;
    private CountDownTimer timer;

    EditText codeInputEditText;
    Button nextButton;
    TextView resendTextView;
    TextView resendLinkTextView;
    ProgressBar spinner;

    //region View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        phone = intent.getStringExtra("phone");
        country = intent.getStringExtra("country");
        number = intent.getStringExtra("number");

        setContentView(R.layout.activity_phone_code);
        configureView();
    }

    public void configureView()
    {
        codeInputEditText = findViewById(R.id.codeInputEditText);
        codeInputEditText.addOnLayoutChangeListener(new RoundedCornersStrokeListener(getResources().getColor(R.color.system_button_background)));
        codeInputEditText.addTextChangedListener(textWatcher);
        resendTextView = findViewById(R.id.resendTextView);
        resendLinkTextView = findViewById(R.id.resendLinkTextView);
        nextButton = findViewById(R.id.nextButton);
        nextButton.addOnLayoutChangeListener(new RoundedCornersListener());
        nextButton.setAlpha(0.5f);
        spinner = findViewById(R.id.progressBar);

        TextView infoTextView = findViewById(R.id.infoTextView);
        infoTextView.setText(getResources().getString(R.string.tr_phone_code_info) + "\n" + phone);

        nextButtonTap();
        resendLinkTap();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopTimer();
    }
    //endregion

    //region Buttons

    private void resendLinkTap()
    {
        resendLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API.verify(country, number, phoneListener);
            }
        });
    }

    private APIListener phoneListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            startTimer();
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    private void nextButtonTap()
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                API.login("phone", phone, codeInputEditText.getText().toString(), codeListener);
            }
        });
    }

    private APIListener codeListener = new APIListener() {

        @Override
        public void success(JSONObject response) {
            spinner.setVisibility(View.INVISIBLE);
            Player player = new Player(response);
            Misc.setCurrentPlayer(player);

            // show profile activity if username is not set
            if(TextUtils.isEmpty(player.username))
            {
                Intent intent = new Intent(PhoneCodeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finishAffinity();
            }
            // show main screen otherwise
            else {
                Misc.toMain();
            }
        }

        @Override
        public void failure(int code, String[] errors) {
            spinner.setVisibility(View.INVISIBLE);
            showErrors(errors);
        }
    };

    //endregion

    //region Timer and TextWatcher

    private void startTimer()
    {
        resendTextView.setVisibility(View.VISIBLE);
        resendLinkTextView.setVisibility(View.INVISIBLE);

        timer = new CountDownTimer(defaultResendTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                resendTextView.setText(getString(R.string.tr_resend_code_time, millisUntilFinished / 1000));
            }

            public void onFinish() {
                resendTextView.setVisibility(View.INVISIBLE);
                resendLinkTextView.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void  stopTimer()
    {
        timer.cancel();
        resendTextView.setVisibility(View.INVISIBLE);
        resendLinkTextView.setVisibility(View.VISIBLE);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            nextButton.setEnabled(s.length() == 6);
            nextButton.setAlpha(s.length() == 6 ? 1.0f : 0.5f);
        }
    };

    //endregion
}
