package com.example.mathhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private int selectedItemPosition;
    private static final int EDIT_POST_REQUEST = 1;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        return view;
    }

    private void loadPosts() {
        if (firestore != null) {
            firestore.collection("posts").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Post> newPostList = new ArrayList<>(); // Create a new list to hold the new data
                    for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Post post = documentChange.getDocument().toObject(Post.class);
                            if (post != null) {
                                newPostList.add(0, post); // Add new items to the new list
                            }
                        }
                    }
                    postList.clear(); // Clear the old list
                    postList.addAll(newPostList); // Add all items from the new list to the old list
                    postAdapter.notifyDataSetChanged(); // Notify the adapter of the data change
                    swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                } else {
                    Toast.makeText(requireContext(), "Failed to load posts: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                }
            });
        }
    }

    @Override
    public void onPostOptionsClicked(View view, int position, Post post) {
        selectedItemPosition = position;
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
                Post post = postList.get(selectedItemPosition);
                Intent intent = new Intent(requireContext(), EditPostActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("description", post.getDescription());
                startActivityForResult(intent, EDIT_POST_REQUEST);
                return true;
            } else if (itemId == R.id.action_delete) {
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
                return true;
            } else if (itemId == R.id.action_rate) {
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void deletePost(int position) {
        Post post = postList.get(position);
        String postId = post.getPostId();

        if (postId != null) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_POST_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String postId = data.getStringExtra("postId");
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                updateEditedPost(postId, title, description);
            }
        }
    }

    private void updateEditedPost(String postId, String title, String description) {
        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);
            if (post.getPostId().equals(postId)) {
                post.setTitle(title);
                post.setDescription(description);
                postAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
}
