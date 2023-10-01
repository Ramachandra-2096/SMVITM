package com.example.smvitm;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.contentTextView.setText(post.getContent());

        // Log the file URL
        Log.d("PostAdapter", "File URL: " + post.getFileUrl());

        if (post.getFileUrl() != null) {
            if (isImageFile(post.getFileUrl())) {
                Log.d("PostAdapter", "Image file detected");
                holder.contentTextView.setText(post.getFileUrl());
                holder.fileImageView.setVisibility(View.VISIBLE);
                holder.pdfView.setVisibility(View.GONE);

                Glide.with(holder.itemView.getContext())
                        .load(post.getFileUrl())
                        .placeholder(R.drawable.iii)
                        .error(R.drawable.ii)
                        .into(holder.fileImageView);

            } else if (isPdfFile(post.getFileUrl())) {
                Log.d("PostAdapter", "PDF file detected");
                // Load PDF using pdfView
                holder.fileImageView.setVisibility(View.GONE);
                holder.pdfView.setVisibility(View.VISIBLE);

                holder.pdfView.fromUri(Uri.parse(post.getFileUrl())).load();
            }
        } else {
            holder.fileImageView.setVisibility(View.GONE);
            holder.pdfView.setVisibility(View.GONE);
        }
    }


    private boolean isImageFile(String fileUrl) {
        return fileUrl.endsWith(".jpg") || fileUrl.endsWith(".jpeg") || fileUrl.endsWith(".png");
    }

    private boolean isPdfFile(String fileUrl) {
        return fileUrl.endsWith(".pdf");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView fileImageView;
        PDFView pdfView;
        TextView contentTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            fileImageView = itemView.findViewById(R.id.fileImageView);
            pdfView = itemView.findViewById(R.id.pdfView);
        }
    }
}
