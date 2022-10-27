package com.example.foodorg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEdt, pwdEdt, cnfEdt;
    private Button registerBtn;
    private ProgressBar loadingPB;
    private FirebaseAuth mAuth;

    private Button returnToMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        emailEdt = findViewById(R.id.emailRegister);
        pwdEdt = findViewById(R.id.passwordRegister);
        cnfEdt = findViewById(R.id.passwordSecondRegister);

        registerBtn = findViewById(R.id.registerButtonConfirm);
        mAuth = FirebaseAuth.getInstance();

        returnToMain = findViewById(R.id.returnToMainFromRegister);

        returnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailEdt.getText().toString();
                String pwd = pwdEdt.getText().toString();
                String cnfpwd = cnfEdt.getText().toString();

                if(!pwd.equals(cnfpwd)){
                    Toast.makeText(RegistrationActivity.this, "Please check both passwords", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(username) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfpwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please add all credentials", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(username,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }
            }
        });

    }
}