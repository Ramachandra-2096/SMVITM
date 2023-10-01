package com.example.smvitm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.smvitm.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Home extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;

    private ImageView profileImage;
     public static int a;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasStoragePermission()) {
            // Permission is not granted, request it
            requestStoragePermission();
        } else {
            // Permission is already granted, you can proceed with your activity
            // ...
        }
        mAuth = FirebaseAuth.getInstance();
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageViewModel imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class); // Initialize ViewModel

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, MainActivity2.class);
            startActivity(intent);
            finish();
        });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_result)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);

        TextView em = headerView.findViewById(R.id.textView43);
        TextView nam = headerView.findViewById(R.id.nametxt);
        profileImage = headerView.findViewById(R.id.profile_image);
        showProgressDialog();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nam.setText(dataSnapshot.child("Name").getValue(String.class));
                    em.setText(dataSnapshot.child("Email").getValue(String.class));

                    a = dataSnapshot.child("Is").getValue(Integer.class);
                    // Load profile image using Glide for smoother loading
                    String profileImageUrl = dataSnapshot.child("ProfileImage").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(Home.this)
                                .load(profileImageUrl)
                                .into(profileImage);
                    }
                    hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching user data: " + error.getMessage());
            }
        });

        // Set long click listener on the profile image
        profileImage.setOnLongClickListener(view -> {
            // Open the gallery to select an image
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            return true; // Consume the long click event
        });

        // Reload the image from SharedPreferences when returning to the activity
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("selectedImageUri", null);
        if (imageUriString != null) {
            Uri selectedImageUri = Uri.parse(imageUriString);
            Glide.with(this).load(selectedImageUri).into(profileImage);
            imageViewModel.setSelectedImageUri(selectedImageUri);
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up Environment...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
               android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void onImageViewClick(View view) {
        // Start the new activity when the ImageView is clicked
        Intent intent = new Intent(this, ZoomedActivity.class);

        // Get the current image URI from Glide and convert it to a string
        Glide.with(this).asBitmap().load(profileImage.getDrawable()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // Save the image to SharedPreferences (Optional, in case you need to access it after activity restarts)
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Convert the Bitmap to a URI string and store it in SharedPreferences
                String imageUriString = getImageUriStringFromBitmap(resource);
                editor.putString("selectedImageUri", imageUriString);
                editor.apply();

                // Pass the image URI as a string extra to the next activity
                intent.putExtra("imageUriString", imageUriString);
                startActivity(intent);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // Handle any error or placeholder if needed
            }
        });
    }


    private String getImageUriStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageUriString = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageUriString;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            // Load the selected image using Glide into the profile image view
            Glide.with(this).load(selectedImageUri).into(profileImage);

            // Save the image URI to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedImageUri", selectedImageUri.toString());
            editor.apply();

            // Save the image to Firebase Storage and update the download URL in Firebase Database
            saveImageToFirebase(selectedImageUri);
        }
    }

    private void saveImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            try {
                // Convert the selected image to a byte array
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Upload the byte array to Firebase Storage
                String userId = mAuth.getCurrentUser().getUid();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId + ".jpg");
                UploadTask uploadTask = storageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Save the download URL to Firebase Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                        userRef.child("ProfileImage").setValue(downloadUri.toString())
                                .addOnSuccessListener(aVoid -> Snackbar.make(findViewById(android.R.id.content), "Image uploaded successfully.", Snackbar.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Log.e("Firebase", "Error uploading image URL: " + e.getMessage()));
                    });
                }).addOnFailureListener(e -> Log.e("Firebase", "Error uploading image: " + e.getMessage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Home.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if ((id == R.id.action_Send)) {
            if (a == 1 || a == 2) {
                Intent intent = new Intent(Home.this, TeacherActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "You don't have access to enter ", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Show a confirmation dialog for exiting the app
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Exit the app
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}
