package com.example.obi_wan.tribe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;//Authorizes user account on firebase
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        //Authenticates Account Creation After Create Button Clicked
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    //Action to be taken at Start of Register Activity
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser(); //the current user

        //If the User is already logged in
        if(currentUser != null){//If user is already registered
            SendUserToMainActivity();//Send User to main activity
        }//end if
    }

    //Sends User to Main Activity
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    //Creates New User Account and Saves Account to FireBase
    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email) || !email.contains(".com")){ //Checks if email field left blank
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(password)){ //Checks if email field left blank
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(confirmPassword)){ //Checks if email field left blank
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
        } else if(password.compareTo(confirmPassword) != 0){ //Checks if email field left blank
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else{
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please Wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){//If Firebase authorizes account creation
                                Toast.makeText(RegisterActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                SendUserToSetUpActivity();//Sends user to account setup
                            } else {//Else if Authorization not successful.
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    //Sends User to setup activity
    private void SendUserToSetUpActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
