package com.example.smvitm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonSignup = findViewById(R.id.buttonLogin2);

        buttonSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate email and password fields
            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            // Show the loading screen with a progress dialog
            showProgressDialog();

            // Sign in with email and password in the background
            loginUserWithEmailAndPassword(email, password);
        });

        // Check if the user is already logged in and email is verified
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                // User is logged in and email is verified, navigate to the next activity
                Intent intent = new Intent(LoginActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void loginUserWithEmailAndPassword(String email, String password) {
        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Hide the loading screen (progress dialog)
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // User login successful and email is verified
                            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                            // Update the login status to true
                            updateLoginStatus();

                            Intent intent = new Intent(LoginActivity.this, Home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Email is not verified, show a toast message and log the user out
                            Toast.makeText(LoginActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        // User login failed
                        Toast.makeText(LoginActivity.this, "Login failed. Please check your Email or password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}
