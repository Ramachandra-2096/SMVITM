package com.example.smvitm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_message_adapter, parent, false);

            holder = new ViewHolder();
            holder.senderTextView = convertView.findViewById(R.id.senderTextView);
            holder.contentTextView = convertView.findViewById(R.id.contentTextView);
            holder.unreadTextView = convertView.findViewById(R.id.unreadTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messages.get(position);

        holder.senderTextView.setText(message.getSender());

        String content = message.getContent();
        if (content != null && content.length() > 30) {
            // Show only the first 100 characters and add ellipsis
            content = content.substring(0, 30) + "...";
        }
        holder.contentTextView.setText(content);

        // Get the messageId of the current message
        String messageId = message.getKey();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId != null) {
            // Check the read status of the message in the database
            DatabaseReference messageRef = databaseReference.child("users")
                    .child(userId)
                    .child("Messages")
                    .child(messageId)
                    .child("isRead");

            messageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean isRead = dataSnapshot.getValue(Boolean.class);
                    if (isRead != null) {
                        message.setRead(isRead);

                        // Update the TextView visibility based on the read status
                        if (message.isRead()) {
                            holder.unreadTextView.setVisibility(View.GONE);
                        } else {
                            holder.unreadTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error, if any
                }
            });

            // Update the TextView visibility based on the read status
            if (message.isRead()) {
                holder.unreadTextView.setVisibility(View.GONE);
            } else {
                holder.unreadTextView.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView senderTextView;
        TextView contentTextView;
        TextView unreadTextView;
    }

    public void updateData(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
}
