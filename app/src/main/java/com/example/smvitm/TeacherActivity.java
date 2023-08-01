package com.example.smvitm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeacherActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private List<String> students;
    private ArrayAdapter<String> studentAdapter;
    private static final String TAG = "TeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        students = new ArrayList<>();

        Spinner spinnerSemester = findViewById(R.id.spinnerYear12);
        Spinner spinnerBranch = findViewById(R.id.spinnerBranch13);
        Spinner spinnerSection = findViewById(R.id.spinnerSection13);
        Spinner spinnerStudent = findViewById(R.id.spinnerStudent);

        String[] semesterOptions = {"ALL ", "1", "2", "3", "4"};
        String[] sectionOptions = {"ALL", "A", "B", "C", "NONE"};
        String[] branchOptions = {"ALL", "Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering", "Artificial Intelligence and Datascience", "Artificial Intelligence and Machine Learning "};

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, semesterOptions);
        spinnerSemester.setAdapter(semesterAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, sectionOptions);
        spinnerSection.setAdapter(sectionAdapter);

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, branchOptions);
        spinnerBranch.setAdapter(branchAdapter);

        spinnerSemester.setSelection(1); // Sets the first item as the default selection
        spinnerSection.setSelection(1);
        spinnerBranch.setSelection(1);

        // Initialize the studentAdapter
        studentAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, students);
        spinnerStudent.setAdapter(studentAdapter);

        // Load students initially with default selections
        loadStudents("1", "Computer Science", "A");

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester = parent.getSelectedItem().toString();
                String branch = spinnerBranch.getSelectedItem().toString();
                String section = spinnerSection.getSelectedItem().toString();
                loadStudents(semester, branch, section);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester = spinnerSemester.getSelectedItem().toString();
                String branch = parent.getSelectedItem().toString();
                String section = spinnerSection.getSelectedItem().toString();
                loadStudents(semester, branch, section);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester = spinnerSemester.getSelectedItem().toString();
                String branch = spinnerBranch.getSelectedItem().toString();
                String section = parent.getSelectedItem().toString();
                loadStudents(semester, branch, section);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadStudents(String selectedYear, String selectedBranch, String selectedSection) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> uniqueStudents = new HashSet<>();
                students.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String year = userSnapshot.child("Year").getValue(String.class);
                    String branch = userSnapshot.child("Branch").getValue(String.class);
                    String section = userSnapshot.child("Section").getValue(String.class);
                    String student = userSnapshot.child("USN").getValue(String.class);

                    if (year != null && branch != null && section != null && student != null &&
                            (selectedYear.equals("ALL") || selectedYear.equals(year)) &&
                            (selectedBranch.equals("ALL") || selectedBranch.equals(branch)) &&
                            (selectedSection.equals("ALL") || selectedSection.equals(section)) &&
                            !uniqueStudents.contains(student)) {
                        students.add(student);
                        uniqueStudents.add(student);
                    }
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load students.", databaseError.toException());
            }
        });
    }
}
