package vn.tiendung.socialnetwork.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.LoginResponse;
import vn.tiendung.socialnetwork.Model.UserModel;
import vn.tiendung.socialnetwork.R;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ View
        edtUsername = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Khởi tạo APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        // Xử lý sự kiện khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(username, password);
            }
        });

        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        UserModel user = new UserModel(username, password);
        Call<LoginResponse> call = apiService.login(user);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    UserModel loggedInUser = loginResponse.getUser();

                    Log.d("LoginSuccess", "User: " + loggedInUser.getUsername());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", loggedInUser.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginError", "Đăng nhập thất bại, code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginError", "Lỗi khi gọi API: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
