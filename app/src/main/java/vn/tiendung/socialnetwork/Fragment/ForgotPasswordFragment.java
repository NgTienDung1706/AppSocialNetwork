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
import vn.tiendung.socialnetwork.UI.ForgotPasswordActivity;

public class ForgotPasswordFragment extends Fragment {
    private EditText edtEmail;
    private Button btnSendOTP;
    private TextView txtBackToLogin;
    private APIService apiService;

    public ForgotPasswordFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgotpassword_fragment, container, false);

        // Ánh xạ View
        edtEmail = view.findViewById(R.id.edtOTP);
        btnSendOTP = view.findViewById(R.id.btnSendOTP);
        txtBackToLogin = view.findViewById(R.id.txtBackToLogin);

        // Khởi tạo APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        // Xử lý sự kiện gửi OTP
        btnSendOTP.setOnClickListener(v -> sendOTP());

        // Quay lại màn hình đăng nhập
        txtBackToLogin.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish(); // Đóng ForgotPasswordActivity để quay lại LoginActivity
            }
        });

        return view;
    }

    private void sendOTP() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        // Gọi API gửi OTP
        apiService.sendOTP(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    // Chuyển sang VerifyOTPFragment
                    if (getActivity() instanceof ForgotPasswordActivity) {
                        ((ForgotPasswordActivity) getActivity()).loadFragment(new VerifyOTPFragment(email));
                    }
                } else {
                    Toast.makeText(getActivity(), "Gửi OTP thất bại! Hãy thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối! Hãy kiểm tra internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
