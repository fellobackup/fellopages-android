package com.bigsteptech.realtimechat.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigsteptech.realtimechat.MessengerActivity;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.listners.FirebaseObservableListeners;
import com.bigsteptech.realtimechat.user.database.FirebaseUserDatabase;
import com.bigsteptech.realtimechat.user.service.PersistedUserService;
import com.bigsteptech.realtimechat.user.service.UserService;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    FirebaseAuth firebaseAuth;
    private Boolean isRegistrationCompleted;
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

        usersDB = messengerDatabaseUtils.getUsersDatabase();
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

            isRegistrationCompleted = false;
            firebaseAuth = FirebaseAuth.getInstance();
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
//        if (!checkFields())
//            return;

        logInUserWithEmailAndPassword();

//        firebaseAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            isRegistrationCompleted = true;
//                            // creating user object
//                            User user = new User("Test User", "test4@seaddons.com", "http://dptw5wbct4ot.cloudfront.net/public/album_photo/b3/40/01/13e74_af01.jpg?c=54f2");
//
//                            usersDB.child(task.getResult().getUser().getUid()).setValue(user);
//
//                            logInUserWithEmailAndPassword();
//                        }
//                    }
//                });
    }

    private void logInUserWithEmailAndPassword() {


        Log.d("LoginActivity", emailEditText.getText().toString()  + " password " + passwordEditText.getText().toString());

        firebaseAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            self = firebaseUser.getUid();
                            usersDB.child(firebaseUser.getUid()).child("isOnline").setValue(1);
                            Log.d("LoginActivitty", firebaseUser.getEmail());
                            setToken();
                            startMainActivity();
                        } else {
                        }
                    }
                });
    }

    private void startMainActivity() {

        userService.initOnlineStatus(self);

        Intent intent = new Intent(mContext, MessengerActivity.class);
        startActivity(intent);
    }

    protected boolean checkFields(){
        if (emailEditText.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordEditText.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setToken() {

        final String token = FirebaseInstanceId.getInstance().getToken();
        fcmTokens.child(self).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fcmTokens.child(self).child(token).setValue("true");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
