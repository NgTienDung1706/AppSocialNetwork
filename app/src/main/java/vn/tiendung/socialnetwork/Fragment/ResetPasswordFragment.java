package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class ResetPasswordFragment extends Fragment {
    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private TextView txtBackToLogin;
    private APIService apiService;
    private String email;

    public ResetPasswordFragment(String email) {
        this.email = email; // Lưu email để gửi cùng với request
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resetpassword_fragment, container, false);

        // Ánh xạ View
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        txtBackToLogin = view.findViewById(R.id.txtBackToLogin);

        // Khởi tạo APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        // Xử lý sự kiện đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> resetPassword());

        // Quay lại màn hình đăng nhập
        txtBackToLogin.setOnClickListener(v -> getActivity().finish());

        return view;
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(getActivity(), "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("newPassword", newPassword);
        requestBody.put("confirmPassword", confirmPassword);

        // Gọi API đặt lại mật khẩu
        apiService.resetPassword(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    // Quay lại màn hình đăng nhập
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Đặt lại mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối! Hãy kiểm tra internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
