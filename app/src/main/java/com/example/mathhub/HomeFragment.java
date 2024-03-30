package com.example.mathhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PostAdapter.OnPostOptionsClickListener {

    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private FirebaseFirestore firestore;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        postList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        postAdapter = new PostAdapter(postList, currentUserId, this);
        recyclerView.setAdapter(postAdapter);

        loadPosts();

        ImageView imageView = view.findViewById(R.id.imageView2);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadPosts() {
        if (firestore != null) {
            firestore.collection("posts").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Post post = documentChange.getDocument().toObject(Post.class);
                            if (post != null) {
                                postList.add(0, post);
                                postAdapter.notifyItemInserted(0);
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPostOptionsClicked(View view, int position, Post post) {
        if (post != null && post.getCreatorUserId() != null && currentUserId != null
                && post.getCreatorUserId().equals(currentUserId)) {
            showPostOptionsMenu(view, position);
        } else {
            showOtherOptionsMenu(view, position);
        }
    }

    private void showPostOptionsMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.post_options_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                // Implement edit functionality
                return true;
            } else if (itemId == R.id.action_delete) {
                // Implement delete functionality
                deletePost(position);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showOtherOptionsMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.menu_post_options2);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_report) {
                // Implement report functionality
                return true;
            } else if (itemId == R.id.action_rate) {
                // Implement rate functionality
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void deletePost(int position) {
        Post post = postList.get(position);
        String postId = post.getPostId(); // Get postId from the post object

        if (postId != null) { // Check if postId is not null
            firestore.collection("posts").document(postId).delete()
                    .addOnSuccessListener(aVoid -> {
                        postList.remove(position);
                        postAdapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Post ID is null, unable to delete post", Toast.LENGTH_SHORT).show();
        }
    }

}
