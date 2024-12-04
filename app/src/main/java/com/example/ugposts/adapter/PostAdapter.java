package com.example.ugposts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ugposts.R;
import com.example.ugposts.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each post
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        // Set the message and created time for each post
        Post post = postList.get(position);
        holder.textViewMessage.setText(post.getMessage());
        holder.textViewDate.setText(post.getCreated_time());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder for each post
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewDate;

        public PostViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}