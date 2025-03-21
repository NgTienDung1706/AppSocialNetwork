package vn.tiendung.socialnetwork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.LoginActivity;

public class VerifyAccontFragment extends Fragment {
    private EditText edtOtp;
    private Button btnVerify;
    private String email; // Nhận email từ fragment trước

    public VerifyAccontFragment() {
        // Constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verifyaccount_fragment, container, false);

        // Ánh xạ view
        edtOtp = view.findViewById(R.id.edtOtp);
        btnVerify = view.findViewById(R.id.btnSendOtp);

        // Nhận dữ liệu từ CreateAccountFragment
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        // Xử lý sự kiện xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            if (validateInput()) {
                verifyOtp();
            }
        });

        return view;
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(edtOtp.getText())) {
            edtOtp.setError("OTP is required!");
            return false;
        }
        return true;
    }

    private void verifyOtp() {
        String otpCode = edtOtp.getText().toString().trim();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("otp", otpCode);
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        Log.d("RegisterData", json);
        Log.d("OTP Input", "OTP Entered: " + otpCode);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.validateOtp(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().string();
                        JSONObject jsonResponse = new JSONObject(message);

                        // Lấy nội dung trong "message"
                        String mess = jsonResponse.has("message") ? jsonResponse.getString("message") : "Đăng ký thành công!";

                        Toast.makeText(getContext(), mess, Toast.LENGTH_SHORT).show();

                        // Xác minh thành công -> Chuyển sang màn hình đăng nhập
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
