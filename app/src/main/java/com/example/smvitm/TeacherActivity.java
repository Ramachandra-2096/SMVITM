package com.example.smvitm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView yearListView;
    private ListView sectionListView;
    private ListView studentListView;
    private List<String> years;
    private List<String> sections;
    private List<Student> students;
    private String selectedYear;
    private String selectedSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        yearListView = findViewById(R.id.yearListview);
        sectionListView = findViewById(R.id.sectionListView);
        studentListView = findViewById(R.id.studentListView);

        years = new ArrayList<>();
        sections = new ArrayList<>();
        students = new ArrayList<>();

        loadYears();
        setupYearListView();
    }

    private void loadYears() {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                years.clear();
                for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                    String year = yearSnapshot.child("Yesr").getValue(String.class);
                    if (year != null && !year.isEmpty()) {
                        years.add(year);
                    }
                }
                setupYearListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TeacherActivity", "Failed to load years.", databaseError.toException());
            }
        });
    }

    private void setupYearListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, years);
        yearListView.setAdapter(adapter);

        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = years.get(position);
                loadSections(selectedYear);
            }
        });
    }

    private void loadSections(String year) {
        databaseReference.child("users").orderByChild("Yesr").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sections.clear();
                for (DataSnapshot sectionSnapshot : dataSnapshot.getChildren()) {
                    String section = sectionSnapshot.child("section").getValue(String.class);
                    if (section != null && !section.isEmpty() && !section.equalsIgnoreCase("no")) {
                        sections.add(section);
                    }
                }
                setupSectionListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TeacherActivity", "Failed to load sections.", databaseError.toException());
            }
        });
    }

    private void setupSectionListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sections);
        sectionListView.setAdapter(adapter);

        sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSection = sections.get(position);
                loadStudents(selectedYear, selectedSection);
            }
        });

        sectionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSection = sections.get(position);
                sendMessageToSection(selectedYear, selectedSection);
                return true;
            }
        });
    }

    private void loadStudents(String year, String section) {
        databaseReference.child("users").orderByChild("Yesr_section").equalTo(year + "_" + section).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                students.clear();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null) {
                        students.add(student);
                    }
                }
                setupStudentListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TeacherActivity", "Failed to load students.", databaseError.toException());
            }
        });
    }

    private void setupStudentListView() {
        ArrayAdapter<Student> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, students);
        studentListView.setAdapter(adapter);

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = students.get(position);
                sendMessageToStudent(student);
            }
        });
    }

    private void sendMessageToStudent(Student student) {
        // Implement logic to send the message to the selected student
        // You can use AlertDialog or other UI components to compose and send the message
        // For simplicity, we'll just show a toast message here.
        Toast.makeText(this, "Sending message to: " + student.name, Toast.LENGTH_SHORT).show();
    }

    private void sendMessageToSection(String year, String section) {
        // Implement logic to send the message to all students in the selected section
        // You can use AlertDialog or other UI components to compose and send the message
        // For simplicity, we'll just show a toast message here.
        Toast.makeText(this, "Sending message to all students in: " + year + ", " + section, Toast.LENGTH_SHORT).show();
    }
}
