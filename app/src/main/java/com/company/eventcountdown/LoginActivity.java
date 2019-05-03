package com.company.eventcountdown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class LoginActivity extends Activity {
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;
    private TextView nonmemberText;

    private FirebaseAuth mAuth;
    private FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        nonmemberText = findViewById(R.id.nonmember_text);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);
                loginAccount();
            }
        });

        nonmemberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start create an account activity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginAccount() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginButton.setEnabled(true);
                            user = mAuth.getCurrentUser(); //The user is signed in
                            Intent intent = new Intent(getBaseContext(), EventsActivity.class);
                            startActivity(intent);
                        } else {
                            loginButton.setEnabled(true);
                            Toast.makeText(getApplicationContext(),"Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
