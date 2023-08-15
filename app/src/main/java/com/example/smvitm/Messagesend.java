package com.example.smvitm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Messagesend extends AppCompatActivity {
    private EditText messageEditText;
    private String targetUserUSN,uu,branch;
    private String userName;
    private  int i=0;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesend);

        TextView t = findViewById(R.id.textView);
        messageEditText = findViewById(R.id.messageEditText);
        Button sendMessageButton = findViewById(R.id.sendMessageButton);

        Intent intent = getIntent();
        uu=null;

        targetUserUSN = intent.getStringExtra("USN");
        uu =intent.getStringExtra("ID");
         if (uu!=null )
         {
             targetUserUSN="Year : "+uu;
         }
         branch=null;
         branch=intent.getStringExtra("Branch");

        if (uu!=null)
        {
            if ( branch !=null)
            targetUserUSN="Year : "+uu+"\nBranch : " +branch;
        }
        t.setText(targetUserUSN);

        sendMessageButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(targetUserUSN, message);
            }
        });
    }

    private void sendMessageToUser(final String targetUSN, final String messageContent) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");


        DatabaseReference usersRef1 = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString());
        usersRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("Name").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (uu!=null&&branch!=null)
        {
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        String year = userSnapshot.child("Year").getValue(String.class);
                        String Branch = userSnapshot.child("Branch").getValue(String.class);

                        if (uu.equals(year) && branch.equals(Branch))
                        {
                            sendMessageWithUserName(userId, userName, messageContent);
                            i = 1;
                        }
                    }

                    if (i == 0) {
                        Toast.makeText(Messagesend.this, "No student found in Year: " + uu + " Branch: " + branch, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Messagesend.this, "Message sent ", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Messagesend.this,TeacherActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        else if(uu!=null &&branch==null)
        {
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        String year = userSnapshot.child("Year").getValue(String.class);
                        if (uu.equals(year)) {
                            sendMessageWithUserName(userId, userName, messageContent);
                            i = 1;
                        }
                    }
                    if (i==0) {
                        Toast.makeText(Messagesend.this, "No student found in Year :" + uu, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Messagesend.this, "Message sent ", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Messagesend.this,TeacherActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }


        usersRef.orderByChild("USN").equalTo(targetUSN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    sendMessageWithUserName(userId, userName, messageContent);
                    Toast.makeText(Messagesend.this, "Message sent ", Toast.LENGTH_SHORT).show();
                }
                Intent intent=new Intent(Messagesend.this,TeacherActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessageWithUserName(String userId, String userName1, String messageContent) {
        // Create a new message node in the Messages section of the database
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Messages");
        DatabaseReference newMessageRef = messagesRef.push();
        String messageKey = newMessageRef.getKey();

        // Create a Message object with the message content and other required fields
        Message message = new Message(messageContent, userName1, false, messageKey);
        newMessageRef.setValue(message);

    }


}
