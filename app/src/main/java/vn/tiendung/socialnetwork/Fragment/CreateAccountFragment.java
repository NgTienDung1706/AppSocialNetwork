package vn.tiendung.socialnetwork.Fragment;

import android.content.Intent;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.UserModel;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.LoginActivity;
import vn.tiendung.socialnetwork.UI.RegisterActivity;

public class CreateAccountFragment extends Fragment {

    private EditText edtUserName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnRegister;
    private APIService apiService;

    public CreateAccountFragment() {
        // Constructor rỗng bắt buộc
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_fragment, container, false);

        // Ánh xạ View
        edtUserName = view.findViewById(R.id.edtUserName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassWord);
        edtConfirmPassword = view.findViewById(R.id.edtConfirm);

        // Lấy instance của Retrofit từ RetrofitClient
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });

        return view;
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(edtUserName.getText())) {
            edtUserName.setError("UserName is required!");
            return false;
        }
        if (TextUtils.isEmpty(edtEmail.getText())) {
            edtEmail.setError("Email is required!");
            return false;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError("Password is required!");
            return false;
        }
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError("Passwords do not match!");
            return false;
        }
        return true;
    }

    private void registerUser() {
        String username = edtUserName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        UserModel request = new UserModel(username, email , password);
        apiService.registerUser(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().string(); // Lấy chuỗi từ response
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putString("email", email); // Gửi email qua để xác thực OTP
                        VerifyAccontFragment otpFragment = new VerifyAccontFragment();
                        otpFragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, otpFragment)
                                .addToBackStack(null)
                                .commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}