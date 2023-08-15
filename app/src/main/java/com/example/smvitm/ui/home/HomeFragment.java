package com.example.smvitm.ui.home;

import android.content.Intent;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smvitm.Home;
import com.example.smvitm.MainActivity;
import com.example.smvitm.R;
import com.example.smvitm.ZoomedActivity;
import com.example.smvitm.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView noPostsText;
    private Button addPostButton;
    private RecyclerView postsRecyclerView;
    private FirebaseFirestore firestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home,container,false);

        noPostsText = rootview.findViewById(R.id.noPostsText);
        postsRecyclerView = rootview.findViewById(R.id.postsRecyclerView);
        firestore = FirebaseFirestore.getInstance();

        Button addButton = rootview.findViewById(R.id.addPostButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ZoomedActivity.class);
                startActivity(intent);
            }
        });

        return rootview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}