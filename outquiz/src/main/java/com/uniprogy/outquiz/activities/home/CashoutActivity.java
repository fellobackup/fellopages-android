package com.uniprogy.outquiz.activities.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.helpers.RoundedCornersStrokeListener;

import org.json.JSONObject;

public class CashoutActivity extends BaseActivity {

    TextView balanceTextView;
    EditText paypalField;
    Button submitButton;

    int balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        balance = intent.getIntExtra("balance", 0);

        setContentView(R.layout.activity_cashout);
        configureView();
    }

    private void configureView()
    {
        balanceTextView = findViewById(R.id.balanceTextView);
        paypalField = findViewById(R.id.paypalField);
        submitButton = findViewById(R.id.submitButton);

        balanceTextView.setText(Misc.moneyFormat(balance));
        paypalField.setText("");

        paypalField.addOnLayoutChangeListener(new RoundedCornersStrokeListener(getResources().getColor(R.color.system_button_background)));
        submitButton.addOnLayoutChangeListener(new RoundedCornersListener());

        submitButtonTap();
    }

    private void submitButtonTap()
    {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paypal = paypalField.getText().toString();
                if(balance > 0 && !TextUtils.isEmpty(paypal))
                {
                    API.cashout(paypal, cashoutListener);
                }
            }
        });
    }

    private APIListener cashoutListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            AlertDialog alertDialog = new AlertDialog.Builder(CashoutActivity.this).create();
            alertDialog.setTitle(getString(R.string.tr_cashout));
            alertDialog.setMessage(getString(R.string.tr_cashout_success));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.tr_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };
}
