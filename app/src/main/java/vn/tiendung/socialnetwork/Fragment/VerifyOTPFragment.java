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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.ForgotPasswordActivity;

public class VerifyOTPFragment extends Fragment {
    private EditText edtOTP;
    private Button btnVerifyOTP;
    private TextView txtResendOTP;
    private APIService apiService;
    private String email;

    public VerifyOTPFragment(String email) {
        this.email = email; // Lưu email để gửi cùng với OTP
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verifyotp_fragment, container, false);

        // Ánh xạ View
        edtOTP = view.findViewById(R.id.edtOTP);
        btnVerifyOTP = view.findViewById(R.id.btnVerifyOTP);
        txtResendOTP = view.findViewById(R.id.txtResendOTP);

        // Khởi tạo APIService
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        // Xử lý sự kiện xác thực OTP
        btnVerifyOTP.setOnClickListener(v -> verifyOTP());

        // Xử lý gửi lại OTP
        txtResendOTP.setOnClickListener(v -> resendOTP());

        return view;
    }

    private void verifyOTP() {
        String otp = edtOTP.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(getActivity(), "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("otp", otp);

        // Gọi API xác thực OTP
        apiService.verifyOTP(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().get("message");
                        if (message != null) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Phản hồi không hợp lệ!", Toast.LENGTH_SHORT).show();
                        }

                        if (getActivity() instanceof ForgotPasswordActivity) {
                            ((ForgotPasswordActivity) getActivity()).loadFragment(new ResetPasswordFragment(email));
                        }
                } else {
                    Toast.makeText(getActivity(), "Mã OTP không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối! Hãy kiểm tra internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOTP() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        apiService.sendOTP(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Gửi lại OTP thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối! Hãy kiểm tra internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
