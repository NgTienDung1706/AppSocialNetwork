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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.LoginResponse;
import vn.tiendung.socialnetwork.Model.UserModel;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.SongOptionsBottomSheet;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    private TextView txtRegister;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Ánh xạ View
        edtUsername = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassWord);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

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

        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        UserModel user = new UserModel(username, password);
        Call<ResponseBody> call = apiService.login(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Chuyển response thành String
                    String responseBody = null;
                    try {
                        responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // Kiểm tra nếu có user
                        if (jsonResponse.has("user")) {
                            JSONObject userJson = jsonResponse.getJSONObject("user");
                            String userId = userJson.getString("_id");
                            String username = userJson.getString("username");
                            String email = userJson.getString("email");

                            // Lưu vào SharedPreferences
                            SharedPrefManager.getInstance(LoginActivity.this).saveUser(userId, username, email);

                            // Chuyển sang MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("LoginError", "Đăng nhập thất bại, code: " + response.code());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // Lấy lỗi từ response trả về
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.has("message") ? errorJson.getString("message") : "Lỗi không xác định!";

                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("LoginError", "Đăng nhập thất bại: " + errorMessage);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Lỗi khi đọc phản hồi từ server!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LoginError", "Lỗi khi gọi API: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }


        });

    }

}
