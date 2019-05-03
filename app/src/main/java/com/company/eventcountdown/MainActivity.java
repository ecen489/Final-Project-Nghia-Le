package com.company.eventcountdown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
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


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Button createButton;
    private EditText emailText;
    private EditText passwordText;

    private TextView memberText;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButton = findViewById(R.id.create_button);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        memberText = findViewById(R.id.member_text);

        mAuth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createButton.setEnabled(false);
                createAnAccount();
            }
        });

        memberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start login activity
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAnAccount() {
        if(validate()) {
            String email = emailText.getText().toString();
            final String password = passwordText.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                emailText.setText("");
                                passwordText.setText("");
                                Toast.makeText(getApplicationContext(),"Success, account created.",Toast.LENGTH_SHORT).show();
                                user = mAuth.getCurrentUser();
                                createButton.setEnabled(true);
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(),"Authentication failed.",Toast.LENGTH_SHORT).show();
                                createButton.setEnabled(true);
                            }
                        }
                    });
        }

    }

    private boolean validate() {
        boolean valid = true;
        String password = passwordText.getText().toString();
        String email = emailText.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            emailText.setError("Invalid email.");
        } else {
            emailText.setError(null);
        }

        if(password.length() < 4) {
            valid = false;
            passwordText.setError("Password must be at least 4 characters.");
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
