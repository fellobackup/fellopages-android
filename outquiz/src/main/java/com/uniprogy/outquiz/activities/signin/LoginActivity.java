package com.uniprogy.outquiz.activities.signin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.WebViewActivity;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends BaseActivity {

//    CallbackManager callbackManager;

    //region View

    private APIListener loginListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            Player player = new Player(response);
            Misc.setCurrentPlayer(player);

            // show profile activity if username is not set
            if (TextUtils.isEmpty(player.username)) {
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
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
            showErrors(errors);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configureView();

//        // setup facebook
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        login("facebook",
//                                loginResult.getAccessToken().getUserId(),
//                                loginResult.getAccessToken().getToken());
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//
//                    }
//                });
//
//        // setup buttons
//        Button loginFacebookButton = findViewById(R.id.loginFacebookButton);
//        loginFacebookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
//            }
//        });
        Button loginPhoneButton = findViewById(R.id.loginPhoneButton);
        loginPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //endregion

    //region Login

    private void configureView() {
        TextView legalInfoTextView = (TextView) findViewById(R.id.legalInfoTextView);

        String termsOfUse = getString(R.string.tr_terms_of_use);
        String privacyPolicy = getString(R.string.tr_privacy_policy);
        String rules = getString(R.string.tr_rules);
        String legalMsg = getString(R.string.tr_accept_terms, termsOfUse, privacyPolicy, rules);

        int termsOfUseIndex = legalMsg.indexOf(termsOfUse);
        int privacyPolicyIndex = legalMsg.indexOf(privacyPolicy);
        int rulesIndex = legalMsg.indexOf(rules);

        SpannableString spannable = new SpannableString(legalMsg);

        // terms of use
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openLegal("terms");
            }
        };
        spannable.setSpan(termsSpan, termsOfUseIndex, termsOfUseIndex + termsOfUse.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // privacy policy
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openLegal("privacy");
            }
        };
        spannable.setSpan(privacySpan, privacyPolicyIndex, privacyPolicyIndex + privacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // rules
        ClickableSpan rulesSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openLegal("rules");
            }
        };
        spannable.setSpan(rulesSpan, rulesIndex, rulesIndex + rules.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        legalInfoTextView.setLinkTextColor(Misc.colorWithAlpha(0.5, getResources().getColor(R.color.text)));
        legalInfoTextView.setHighlightColor(Color.TRANSPARENT);
        legalInfoTextView.setText(spannable);
        legalInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // rounded buttons
        Button loginPhoneButton = findViewById(R.id.loginPhoneButton);
        loginPhoneButton.addOnLayoutChangeListener(new RoundedCornersListener());
        Button loginFacebookButton = findViewById(R.id.loginFacebookButton);
        loginFacebookButton.addOnLayoutChangeListener(new RoundedCornersListener());
    }

    private void openPhoneLogin() {
        Intent i = new Intent(this, PhoneInputActivity.class);
        startActivity(i);
    }

    private void login(String type, String id, String verification) {
        API.login(type, id, verification, loginListener);
    }

    //endregion

    //region WebView

    private void openLegal(String type) {
        String docTitle = "";
        switch (type) {
            case "terms":
                docTitle = getString(R.string.tr_terms_of_use);
                break;
            case "privacy":
                docTitle = getString(R.string.tr_privacy_policy);
                break;
            case "rules":
                docTitle = getString(R.string.tr_rules);
                break;
        }

        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("docTitle", docTitle);
        i.putExtra("docUrl", Misc.docUrl(type));
        startActivity(i);
    }

    //endregion


}
