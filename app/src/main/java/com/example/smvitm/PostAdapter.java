package com.example.smvitm;

<<<<<<<<< Temporary merge branch 1
=========
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
>>>>>>>>> Temporary merge branch 2
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
<<<<<<<<< Temporary merge branch 1
=========
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
>>>>>>>>> Temporary merge branch 2

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

<<<<<<<<< Temporary merge branch 1
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
=========
        if (post.getFileUrl() != null) {
            String fileUrl = post.getFileUrl();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(fileUrl);

            // Retrieve file metadata to get content type
            storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    String contentType = storageMetadata.getContentType();

                    if (isImageFile(contentType)) {
                        Log.d("PostAdapter", "Image file detected");
                        holder.fileImageView.setVisibility(View.VISIBLE);
                        holder.pdfView.setVisibility(View.GONE);

                        Glide.with(holder.itemView.getContext())
                                .load(post.getFileUrl())
                                .placeholder(R.drawable.iii)
                                .error(R.drawable.ii)
                                .into(holder.fileImageView);

                        // Set an OnClickListener to open the image in the gallery
                        holder.fileImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openImageInGallery(holder.itemView.getContext(), post.getFileUrl());
                            }
                        });

                    } else if (isPdfFile(contentType)) {
                        Log.d("PostAdapter", "PDF file detected");
                        holder.fileImageView.setVisibility(View.GONE);
                        holder.pdfView.setVisibility(View.VISIBLE);

                        holder.pdfView.fromUri(Uri.parse(post.getFileUrl())).load();
                    }
                }
            });
>>>>>>>>> Temporary merge branch 2
        } else {
            holder.fileImageView.setVisibility(View.GONE);
            holder.pdfView.setVisibility(View.GONE);
        }
    }

<<<<<<<<< Temporary merge branch 1

    private boolean isImageFile(String fileUrl) {
        return fileUrl.endsWith(".jpg") || fileUrl.endsWith(".jpeg") || fileUrl.endsWith(".png");
    }

    private boolean isPdfFile(String fileUrl) {
        return fileUrl.endsWith(".pdf");
=========
    // Check if content type represents an image
    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    // Check if content type represents a PDF
    private boolean isPdfFile(String contentType) {
        return contentType != null && contentType.equals("application/pdf");
>>>>>>>>> Temporary merge branch 2
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
<<<<<<<<< Temporary merge branch 1
=========
        }
    }

    // Method to open an image in the mobile gallery
    private void openImageInGallery(Context context, String imageUrl) {
        // Create an Intent to view the image
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imageUrl), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no suitable viewer app is available
            Log.e("PostAdapter", "No image viewer app found.");
>>>>>>>>> Temporary merge branch 2
        }
    }
}
