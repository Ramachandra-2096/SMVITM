package com.example.smvitm;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private ListView messageListView;
    private MessageAdapter messageAdapter;

    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        messageListView = findViewById(R.id.messageListView);

        // Get a reference to the "messages" node in the database
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("message");


        // Attach a ValueEventListener to retrieve messages from the database
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();

                // Loop through the dataSnapshot to retrieve messages
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }

                // Update the ListView with the messages
                messageAdapter = new MessageAdapter(MainActivity2.this, messages);
                messageListView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(MainActivity2.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set item click listener for the ListView
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message selectedMessage = (Message) parent.getItemAtPosition(position);

                // Mark the message as read
                selectedMessage.setRead(true);
                messagesRef.child(selectedMessage.getKey()).setValue(selectedMessage);

                // Open a new activity to display the message content
                Intent intent = new Intent(MainActivity2.this, MessageDetailsActivity.class);
                intent.putExtra("messageContent", selectedMessage.getContent());
                startActivity(intent);
            }
        });

        // Set item long click listener for the ListView
        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Message selectedMessage = (Message) parent.getItemAtPosition(position);

                // Show a confirmation dialog for message deletion
                showDeleteConfirmationDialog(selectedMessage);

                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the message from the database
                messagesRef.child(message.getKey()).removeValue();

                Toast.makeText(MainActivity2.this, "Message deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // Handle sign out button click
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity2.this, LoginActivity.class));
        finish();
    }
    @Override
    public void onBackPressed() {
        // Start the previous activity
        startActivity(new Intent(MainActivity2.this, Home.class));
        finish();
    }
}
