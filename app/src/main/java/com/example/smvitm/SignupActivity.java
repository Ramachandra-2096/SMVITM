package com.example.smvitm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextUSN, editTextSemester, editTextSection, editTextAge;
    private Button buttonSignup;
    private CheckBox checkBoxAdmin, checkBoxTeacher;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sighnupactivity);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextUSN = findViewById(R.id.editTextUSN);

        Spinner spinnerSemester = findViewById(R.id.spinnerSemester);
        Spinner spinnerSection = findViewById(R.id.spinnerSection);
        Spinner spinnerBranch = findViewById(R.id.spinnerBranch);

        editTextAge = findViewById(R.id.editTextAge);


        buttonSignup = findViewById(R.id.buttonSignup);

        String[] semesterOptions = {"Select Current year ","1", "2", "3", "4"};
        String[] sectionOptions = {"Select Section","A", "B", "C","NONE"};
        String[] branchOptions = {"Select Branch","Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering","Artificial Intelligence and Datascience","Artificial Intelligence and Machine Learning "};

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, semesterOptions);
        spinnerSemester.setAdapter(semesterAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, sectionOptions);
        spinnerSection.setAdapter(sectionAdapter);

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, branchOptions);
        spinnerBranch.setAdapter(branchAdapter);

        spinnerSemester.setSelection(0); // Sets the first item as the default selection
        spinnerSection.setSelection(0);
        spinnerBranch.setSelection(0);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String usn = editTextUSN.getText().toString().trim();
                String semester = spinnerSemester.getSelectedItem().toString();
                String Branch = spinnerBranch.getSelectedItem().toString();
                String section = spinnerSection.getSelectedItem().toString();
                String age = editTextAge.getText().toString().trim();

                // Validate fields
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || usn.isEmpty() || semester.isEmpty() && Integer.parseInt(semester) <= 4 || section.isEmpty() || age.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user account with email and password using Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User signup successful
                                    Toast.makeText(SignupActivity.this, "Sign up successful. Please check your email for verification.", Toast.LENGTH_SHORT).show();

                                    // Get the user ID of the newly created user
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        // Send email verification
                                        sendEmailVerification();

                                            DatabaseReference userRef = mDatabase.child("users").child(userId);
                                            userRef.child("Name").setValue(name);
                                            userRef.child("USN").setValue(usn);
                                            userRef.child("Email").setValue(email);
                                            userRef.child("Year").setValue(semester);
                                            userRef.child("Branch").setValue(Branch);
                                            userRef.child("Section").setValue(section);
                                            userRef.child("Age").setValue(age);
                                            userRef.child("Is").setValue(0);

                                            // Create a default welcome message for the user
                                            DatabaseReference messagesRef = userRef.child("Messages");
                                            DatabaseReference newMessageRef = messagesRef.push();
                                            String messageId = newMessageRef.getKey();

                                            String senderName = "Developer";
                                            String messageContent = "Hello, "+name+" welcome to our app ";
                                            boolean isRead = false;

                                            Message message = new Message(messageContent, senderName, isRead, messageId);
                                            newMessageRef.setValue(message);

                                        buttonSignup.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    // User signup failed
                                    Toast.makeText(SignupActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Email verification sent. Please check your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "Failed to send email verification.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
