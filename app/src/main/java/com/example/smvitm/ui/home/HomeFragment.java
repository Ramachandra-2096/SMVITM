package com.example.smvitm.ui.home;

import static com.example.smvitm.Home.a;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smvitm.NewPostActivity;
import com.example.smvitm.Post;
import com.example.smvitm.PostAdapter;
import com.example.smvitm.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button addPostButton;
    private TextView noPostsText;
    private RecyclerView postsRecyclerView;
    private FirebaseFirestore firestore;
    private List<Post> postList;
    private PostAdapter postAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        addPostButton = rootView.findViewById(R.id.addPostButton);
        noPostsText = rootView.findViewById(R.id.noPostsText);
        postsRecyclerView = rootView.findViewById(R.id.postsRecyclerView);

        
        if (a != 2) {
            addPostButton.setVisibility(View.INVISIBLE);
        }
        firestore = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(postAdapter);

        CollectionReference postsCollection = firestore.collection("posts");
        postsCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            postList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Post post = documentSnapshot.toObject(Post.class);
                postList.add(post);
            }

            postAdapter.notifyDataSetChanged();
        });

        addPostButton.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NewPostActivity.class));
        });

        return rootView;
    }
}
