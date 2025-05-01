package vn.tiendung.socialnetwork.Repository;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.UserProfile;

public class UserRepository {

    private final APIService apiService;

    public interface UserCallback {
        void onSuccess(UserProfile userProfile);
        void onError(String message);
    }

    public UserRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public void getUserProfile(String userId, UserCallback callback) {
        Call<UserProfile> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tìm thấy người dùng");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
