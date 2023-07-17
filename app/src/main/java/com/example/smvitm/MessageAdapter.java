package com.example.smvitm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
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
        holder.contentTextView.setText(message.getContent());

        if (message.isRead()) {
            holder.unreadTextView.setVisibility(View.GONE);
        } else {
            holder.unreadTextView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView senderTextView;
        TextView contentTextView;
        TextView unreadTextView;
    }
}
