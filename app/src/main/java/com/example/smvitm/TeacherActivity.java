package com.example.smvitm;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherActivity extends AppCompatActivity {

    // Sample data for years and branches
    private static final String[] years = {"1", "2", "3", "4"};
    private static final String[] branches = {"ALL","Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering","Artificial Intelligence and Datascience","Artificial Intelligence and Machine Learning "};

    private Spinner yearSpinner;
    private Spinner branchSpinner;
    private Button searchButton;
    private ListView studentListView;
    private ProgressBar loadingIndicator;
    private TextView emptyStateTextView;

    private List<Map<String, String>> students;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        yearSpinner = findViewById(R.id.spinnerSemester123);
        branchSpinner = findViewById(R.id.spinnerBranch46);
        searchButton = findViewById(R.id.searchButton);
        studentListView = findViewById(R.id.studentListView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Set up ArrayAdapter for the yearSpinner and branchSpinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, branches);

        yearSpinner.setAdapter(yearAdapter);
        branchSpinner.setAdapter(branchAdapter);

        students = new ArrayList<>();

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Attach a listener to retrieve data from Firebase
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Data from Firebase has been added (new student)
                Map<String, String> studentData = (Map<String, String>) dataSnapshot.getValue();
                students.add(studentData);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Data from Firebase has been updated (existing student)
                Map<String, String> studentData = (Map<String, String>) dataSnapshot.getValue();
                int index = findStudentIndex(studentData.get("USN"));
                if (index >= 0) {
                    students.set(index, studentData);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Data from Firebase has been removed (student removed)
                Map<String, String> studentData = (Map<String, String>) dataSnapshot.getValue();
                int index = findStudentIndex(studentData.get("USN"));
                if (index >= 0) {
                    students.remove(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Data from Firebase has been moved (not used in this example)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // An error occurred while retrieving data from Firebase
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the loading indicator and hide the ListView
                loadingIndicator.setVisibility(View.VISIBLE);
                studentListView.setVisibility(View.GONE);
                emptyStateTextView.setVisibility(View.GONE);

                // Get selected year and branch
                String selectedYear = yearSpinner.getSelectedItem().toString();
                String selectedBranch = branchSpinner.getSelectedItem().toString();

                // Perform the search asynchronously (using AsyncTask, ViewModel, or other methods)

                // For this example, let's just simulate a delay with postDelayed
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Hide the loading indicator
                        loadingIndicator.setVisibility(View.GONE);

                        // Search for matching students
                        List<String> matchingUSNs = new ArrayList<>();
                        for (Map<String, String> student : students) {
                            String studentYear = student.get("Year");
                            String studentBranch = student.get("Branch");

                            if (studentYear != null && studentBranch != null && studentYear.equals(selectedYear) && studentBranch.equals(selectedBranch)) {
                                matchingUSNs.add(student.get("USN"));
                            }
                        }

                        // Display the results or the empty state view
                        if (!matchingUSNs.isEmpty()) {
                            ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(TeacherActivity.this, android.R.layout.simple_list_item_1, matchingUSNs);
                            studentListView.setAdapter(studentAdapter);
                            studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent=new Intent(TeacherActivity.this, Messagesend.class);
                                    intent.putExtra("USN",matchingUSNs.get(i).toString());
                                    startActivity(intent);
                                }
                            });
                            studentListView.setVisibility(View.VISIBLE);
                            emptyStateTextView.setVisibility(View.GONE);
                        } else {
                            studentListView.setVisibility(View.GONE);
                            emptyStateTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 2000); // Simulate a 2-second search delay (remove this in the actual implementation)
            }
        });
    }

    private int findStudentIndex(String usn) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).get("USN").equals(usn)) {
                return i;
            }
        }
        return -1;
    }
}
