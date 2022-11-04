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
 *  RegistrationActivity is the activity a user sees when clicking on the register button from
 *  LoginActivity. Here the user must create a new appropriate email and a specific password for
 *  an account so that they can then login into their respective account database.
 *  <p>
 * The Activity contains buttons that allow a user to Register with the rightly formatted details,
 * or go to RegistrationActivity and register.
 *
 * @author amman1
 * @author mohaimin
 *
 */

public class RegistrationActivity extends AppCompatActivity {

    // The TextInputEditTexts that input the new email, password and confirmedPassword
    private TextInputEditText emailEditText, pwdEditText, cnfPwdEditText;

    // Buttons to register or directly go to LoginPage
    private Button registerBtn, returnToLoginMain;

    // Authentication for Firebase
    private FirebaseAuth mAuth;

    /**
     * The onCreate Method
     * @param savedInstanceState Bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize the editTexts with their respective id's
        emailEditText = findViewById(R.id.emailRegister);
        pwdEditText = findViewById(R.id.passwordRegister);
        cnfPwdEditText = findViewById(R.id.passwordSecondRegister);


        // Initialize the Buttons with their respective id's
        registerBtn = findViewById(R.id.registerButtonConfirm);
        returnToLoginMain = findViewById(R.id.returnToMainFromRegister);

        mAuth = FirebaseAuth.getInstance();


        // OnClickListener for the return to LoginActivity page
        returnToLoginMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start the LoginActivity
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });


        // OnClickListener for adding the new user with appropriate Email and password
        // as well as changing activity to LoginActivity once done
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // String values for each EditText entry
                String emailRegister = emailEditText.getText().toString();
                String pwdRegister = pwdEditText.getText().toString();
                String cnfPwdRegister = cnfPwdEditText.getText().toString();


                // Check first to see if they are appropriate string values
                // First check password equals confirm password
                if(!pwdRegister.equals(cnfPwdRegister)){
                    Toast.makeText(RegistrationActivity.this, "Please check both passwords", Toast.LENGTH_SHORT).show();

                } // Here check if fields are empty
                else if(TextUtils.isEmpty(emailRegister) && TextUtils.isEmpty(pwdRegister) && TextUtils.isEmpty(cnfPwdRegister)) {
                    Toast.makeText(RegistrationActivity.this, "Please add all credentials", Toast.LENGTH_SHORT).show();
                }else{
                    // If task is successful, that is the credentials entered are correct then
                    // we go to homePage activity and add the user
                    mAuth.createUserWithEmailAndPassword(emailRegister,pwdRegister).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // Go to LoginActivity to enter details
                                Toast.makeText(RegistrationActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(RegistrationActivity.this, "Failed to Register, ensure correct email & password > 6 characters", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }
            }
        });

    }
}