package com.example.smvitm;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

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
                        holder.downloadButton.setVisibility(View.VISIBLE);

                        Glide.with(holder.itemView.getContext())
                                .load(post.getFileUrl())
                                .placeholder(R.drawable.iii)
                                .error(R.drawable.iii)
                                .into(holder.fileImageView);

                        // Set an OnClickListener to open the image in the gallery
                        holder.fileImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openImageInGallery(holder.itemView.getContext(), post.getFileUrl());
                            }
                        });
                        // Set an OnClickListener to download the image
                        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadFile(holder.itemView.getContext(), post.getFileUrl(), "image/*");
                            }
                        });

                    } else if (isPdfFile(contentType)) {
                        Log.d("PostAdapter", "PDF file detected");
                        holder.fileImageView.setVisibility(View.VISIBLE);
                        holder.pdfView.setVisibility(View.GONE);
                        holder.downloadButton.setVisibility(View.VISIBLE);
                        Glide.with(holder.itemView.getContext())
                                .load(R.drawable.pdf)
                                .into(holder.fileImageView);
                        // Set an OnClickListener to open the PDF using Google Drive viewer
                        holder.fileImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openPdfInBrowser(holder.itemView.getContext(), post.getFileUrl());
                            }
                        });
                        // Set an OnClickListener to download the PDF
                        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadFile(holder.itemView.getContext(), post.getFileUrl(), "application/pdf");
                            }
                        });
                    } else {
                        holder.fileImageView.setVisibility(View.GONE);
                        holder.pdfView.setVisibility(View.GONE);
                        holder.downloadButton.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    // Method to open a PDF in the user's default browser
    private void openPdfInBrowser(Context context, String pdfUrl) {
        // Use Google Drive's viewer URL to open the PDF directly
        String googleDriveViewerUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(googleDriveViewerUrl));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("PostAdapter", "No PDF viewer app found.");
            }
        } else {
            Log.e("PostAdapter", "No app can handle the PDF viewer intent.");
        }
    }

    // Check if content type represents an image
    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    // Check if content type represents a PDF
    private boolean isPdfFile(String contentType) {
        return contentType != null && contentType.equals("application/pdf");
    }

    // Method to open an image in the mobile gallery
    private void openImageInGallery(Context context, String imageUrl) {
        // Create an Intent to view the image
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imageUrl), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Verify that there's an app to handle this intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                context.startActivity(Intent.createChooser(intent, "Open image with"));
            } catch (ActivityNotFoundException e) {
                // Handle the case where no suitable viewer app is available
                Log.e("PostAdapter", "No image viewer app found.");
            }
        } else {
            // Handle the case where no app can handle the intent
            Log.e("PostAdapter", "No app can handle the image viewer intent.");
        }
    }

    // Method to download a file
    private void downloadFile(Context context, String fileUrl, String mimeType) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading");
        request.setDescription("File downloading...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType(mimeType);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView fileImageView;
        PDFView pdfView;
        TextView contentTextView;
        Button downloadButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            fileImageView = itemView.findViewById(R.id.fileImageView);
            pdfView = itemView.findViewById(R.id.pdfView);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }
}
