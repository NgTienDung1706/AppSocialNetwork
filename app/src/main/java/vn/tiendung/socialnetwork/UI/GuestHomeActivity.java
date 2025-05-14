package vn.tiendung.socialnetwork.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;

public class GuestHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private PostAdapter postAdapter;

    private ProgressBar progressBar;

    private Button btnLoginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_home);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewPosts);
        btnLoginNow = findViewById(R.id.btnLoginNow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestHomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loadPublicPosts();
    }

    private void loadPublicPosts() {
        progressBar.setVisibility(View.VISIBLE);

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Post>> call = apiService.getPublicPosts(); // API này bạn cần định nghĩa

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    postAdapter = new PostAdapter(GuestHomeActivity.this, posts,true);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Toast.makeText(GuestHomeActivity.this, "Không tải được bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GuestHomeActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
