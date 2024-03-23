package com.example.mathhub;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        postList = new ArrayList<>();

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        loadPosts();

        ImageView imageView = view.findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadPosts() {
        firestore.collection("posts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post post = documentChange.getDocument().toObject(Post.class);
                        postList.add(post);
                        postAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
