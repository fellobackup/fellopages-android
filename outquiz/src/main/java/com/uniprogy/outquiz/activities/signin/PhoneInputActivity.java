package com.uniprogy.outquiz.activities.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;
import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.helpers.RoundedCornersStrokeListener;

import org.json.JSONObject;

public class PhoneInputActivity extends BaseActivity {

    CountryCodePicker countryPicker;
    EditText numberField;
    Button nextButton;
    Context context;

    //region View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);
        context = getApplicationContext();
        configureView();
    }

    public void configureView()
    {
        countryPicker = findViewById(R.id.countryPicker);
        countryPicker.addOnLayoutChangeListener(new RoundedCornersStrokeListener(getResources().getColor(R.color.system_button_background)));
        numberField = findViewById(R.id.numberField);
        numberField.addOnLayoutChangeListener(new RoundedCornersStrokeListener(getResources().getColor(R.color.system_button_background)));
        nextButton = findViewById(R.id.nextButton);
        nextButton.addOnLayoutChangeListener(new RoundedCornersListener());

        nextButtonTap();
    }

    //endregion

    //region Phone Code Request

    private void nextButtonTap()
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = countryPicker.getSelectedCountryNameCode();
                String number = numberField.getText().toString();

                if(!TextUtils.isEmpty(country) && !TextUtils.isEmpty(number)) {
                    API.verify(country, number, phoneListener);
                }
            }
        });
    }

    private APIListener phoneListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            String phone = response.optString("phone");
            Intent i = new Intent(PhoneInputActivity.this, PhoneCodeActivity.class);
            i.putExtra("phone", phone);
            i.putExtra("country", countryPicker.getSelectedCountryNameCode());
            i.putExtra("number", numberField.getText().toString());

            startActivity(i);
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    //endregion

}
