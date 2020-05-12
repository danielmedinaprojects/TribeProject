package com.example.obi_wan.tribe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;//the login button
    private EditText UserEmail, UserPassword;//useremail and userpassword edittexts
    private TextView NeedNewAccountLink;//the need new account UI link
    private FirebaseAuth mAuth; //The Firebase User Authorization
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.register_account_link);
        mAuth = FirebaseAuth.getInstance();//Get Instance of Firebase User Authentication
        loadingBar = new ProgressDialog(this);//Loading bar for account login

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() { //set listener for edit text link/textview
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();//Sends User to Register Activity page
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {//Adds Action Listener to Login Button
            @Override
            public void onClick(View view) {
                InitUserLogin();//Initializes User login
            }
        });
    }

    //Sends User to Main activity if logged in to account.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser(); //the current user

        //If the User is already logged in
        if(currentUser != null){//start if
            SendUserToMainActivity();//Send User to main activity
        }//end if else
    }

    //Initializes User Login
    private void InitUserLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please Wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password) //Firebase User Sign In Authentication
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){//If User Sign In Authorized
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                SendUserToMainActivity();
                            } else{ //If User Sign In Unsuccessful
                                loadingBar.dismiss();
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //Sends User to Main Activity
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    //Sends User to Register Activity page
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        //finish();
    }
}
