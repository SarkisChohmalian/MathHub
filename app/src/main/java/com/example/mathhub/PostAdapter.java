package com.example.mathhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private String currentUserId;
    private OnPostOptionsClickListener optionsClickListener;

    public PostAdapter(List<Post> postList, String currentUserId, OnPostOptionsClickListener optionsClickListener) {
        this.postList = postList;
        this.currentUserId = currentUserId;
        this.optionsClickListener = optionsClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPosts(List<Post> posts) {
        postList.clear();
        postList.addAll(posts);
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView threeDots;
        private TextView replyText;
        private ImageView replyPic;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            threeDots = itemView.findViewById(R.id.three_dots);
            replyText = itemView.findViewById(R.id.reply_text);
            replyPic = itemView.findViewById(R.id.reply_pic);

            threeDots.setOnClickListener(v -> {
                if (optionsClickListener != null) {
                    optionsClickListener.onPostOptionsClicked(v, getAdapterPosition(), postList.get(getAdapterPosition()));
                }
            });

            replyText.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, CommentsActivity.class);
                String postId = postList.get(getAdapterPosition()).getPostId();
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            });

            replyPic.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, CommentsActivity.class);
                String postId = postList.get(getAdapterPosition()).getPostId();
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            });

        }

        public void bind(Post post) {
            titleTextView.setText(post.getTitle());
            descriptionTextView.setText(post.getDescription());
            threeDots.setVisibility(View.VISIBLE);
        }
    }

    public interface OnPostOptionsClickListener {
        void onPostOptionsClicked(View view, int position, Post post);
    }
}
