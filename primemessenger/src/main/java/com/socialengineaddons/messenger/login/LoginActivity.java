package com.socialengineaddons.messenger.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.socialengineaddons.messenger.MessengerActivity;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.listners.FirebaseObservableListeners;
import com.socialengineaddons.messenger.user.database.FirebaseUserDatabase;
import com.socialengineaddons.messenger.user.service.PersistedUserService;
import com.socialengineaddons.messenger.user.service.UserService;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private DatabaseReference usersDB, fcmTokens;
    private Context mContext;
    private String self;
    private UserService userService;
    private MessengerDatabaseUtils messengerDatabaseUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        messengerDatabaseUtils.initializeMessengerApp(mContext);
        fcmTokens = messengerDatabaseUtils.getFcmTokenDatabase();

        FirebaseObservableListeners firebaseObservableListeners = new FirebaseObservableListeners();
        FirebaseUserDatabase userDatabase = new FirebaseUserDatabase(messengerDatabaseUtils.getMessengerDatabaseInstance(),
                firebaseObservableListeners);
        userService = new PersistedUserService(userDatabase);

        if(messengerDatabaseUtils.getCurrentUserId() != null){
            self = messengerDatabaseUtils.getCurrentUserId();
            startMainActivity();
        } else {
            initViews();
        }
    }

    private void initViews() {
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        presenter.stopPresenting();
    }

    @Override
    public void onClick(View view) {


        Log.d(LoginActivity.class.getSimpleName(),  "coming here");

        messengerDatabaseUtils.loginInWithEmailAndPassword(mContext, emailEditText.getText().toString(),
                passwordEditText.getText().toString(), true, 0);
    }

    private void startMainActivity() {

        userService.initOnlineStatus(self);

        Intent intent = new Intent(mContext, MessengerActivity.class);
        startActivity(intent);
    }

    protected boolean checkFields(){
        if (emailEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
