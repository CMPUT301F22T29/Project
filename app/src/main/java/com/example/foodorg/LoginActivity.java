package com.example.foodorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 *  LoginActivity is the first activity a user sees when opening the app. Activity
 *  where the user must enter a specific email and a specific password from an account
 *  they have already made, to then login into their respective account database.
 *  <p>
 * The Activity also contains buttons that allow a user to Login with the right details,
 * or go to RegistrationActivity and register.
 *
 * @author amman1
 * @author mohaimin
 *
 */

public class LoginActivity extends AppCompatActivity {

    // The TextInputEditTexts that input the email and password
    private TextInputEditText emailLoginlEdt, passwordLoginEdit;

    // The Buttons for Logging In and Register Page
    private Button loginBtn;
    private Button registerBtn;

    // The Authentication for Firebase to authenticate users
    private FirebaseAuth mAuth;


    /**
     * The onCreate Method
     * @param savedInstanceState Bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // First initialize the EditTexts & the Buttons
        // based on their respective id's
        emailLoginlEdt = findViewById(R.id.emailLoginMain);
        passwordLoginEdit = findViewById(R.id.passwordLoginMain);
        loginBtn = findViewById(R.id.LoginButtonMain);
        registerBtn = findViewById(R.id.RegisterButtonFromMain);

        // We also get the instance of our Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // A setOnClickListener for the registerBtn
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If user would like to go to RegistrationActivity then start that activity
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });


        // A setOnClickListener for the registerBtn
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // First we get the String values of the email and the pwd
                String email = emailLoginlEdt.getText().toString();
                String pwd = passwordLoginEdit.getText().toString();

                // Here we check to see if all the credentials are entered
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)){
                    Toast.makeText(LoginActivity.this, "Please enter all credentials", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // If task is successful, that is the credentials entered are correct then
                                // we go to homePage activity based on the authenticated user
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, HomePageActivity.class);
                                startActivity(i);
                                finish();

                            }else{

                                //Else we show respective Toast error
                                Toast.makeText(LoginActivity.this, "Failed to Login, Click Register or ensure correct email & password > 6 characters", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }
}