package com.example.ugposts.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ugposts.adapter.PostAdapter;
import com.example.ugposts.model.Post;
import com.example.ugposts.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    private static final String FACEBOOK_PAGE_ID = "UG.DICIS.SecAcad";
    private static final String ACCESS_TOKEN = "2253006595068034|naeKmeg-SLh1WDVF7gy9-2vf1TA";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewPosts;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        fetchFacebookPosts();

        return root;
    }

    private void fetchFacebookPosts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://graph.facebook.com/" + FACEBOOK_PAGE_ID + "/posts?access_token=" + ACCESS_TOKEN);

                    // Conexion
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setDoInput(true);
                    urlConnection.connect();

                    // Respuesta
                    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    StringBuilder response = new StringBuilder();
                    int data = inputStreamReader.read();
                    while (data != -1) {
                        response.append((char) data);
                        data = inputStreamReader.read();
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    // Publicaciones en forma de objeto
                    List<Post> posts = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject postObject = dataArray.getJSONObject(i);
                        String message = postObject.optString("message", "No message");
                        String createdTime = postObject.optString("created_time", "No time available");

                        Post post = new Post(message, createdTime);
                        posts.add(post);
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            postList.clear();
                            postList.addAll(posts);
                            postAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    Log.e("HomeFragment", "Error fetching posts", e);
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}