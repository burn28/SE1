package com.example.se1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText rEmail;
    private EditText rUsername;
    private EditText rPassword;
    private Button rOk;
    private Button rCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rEmail = findViewById(R.id.editTextTextEmailAddress2);
        rUsername = findViewById(R.id.editTextTextPersonName);
        rPassword = findViewById(R.id.editTextTextPassword2);
        rOk = findViewById(R.id.ok_button);
        rCancel = findViewById(R.id.cancel_button);

        rOk.setOnClickListener(this);
        rCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                register();
                break;
            case R.id.cancel_button:
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                break;
        }
    }

    private void register() {
        String email = rEmail.getText().toString().trim();
        String password = rPassword.getText().toString().trim();
        System.out.println(password);
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if(!password.isEmpty()){
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Register fail!", Toast.LENGTH_SHORT).show();
                        System.out.println(e.getMessage());
                    }
                });
            }
            else {
                rPassword.setError("Enter your password");
            }
        }
        else if(email.isEmpty()){
            rEmail.setError("Enter an email!");
        }
        else {
            rEmail.setError("Enter a valid email!");
        }
    }
}