package vn.tiendung.socialnetwork.Repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.UserProfile;

public class ProfileRepository {
    private final APIService apiService;

    public ProfileRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public interface ProfileCallback {
        void onSuccess(UserProfile userProfile);
        void onError(String errorMessage);
        void onSuccess();
    }

    public void getUserProfile(String userId,String userIdMe ,ProfileCallback callback) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProfileUser(userId,userIdMe).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thông tin người dùng");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }


    public void addFriend(String userId,String userIdMe ,ProfileCallback callback) {
        apiService.addFriend(userId,userIdMe).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(); // Cập nhật lại thông tin người dùng
                } else {
                    callback.onError("Không thể gửi yêu cầu kết bạn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void accept(String userId,String userIdMe ,ProfileCallback callback) {
        apiService.acceptFriend(userId,userIdMe).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(); // Cập nhật lại thông tin người dùng
                } else {
                    callback.onError("Không thể chấp nhận yêu cầu kết bạn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void reject(String userId,String userIdMe ,ProfileCallback callback) {
        apiService.rejectFriend(userId,userIdMe).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(); // Cập nhật lại thông tin người dùng
                } else {
                    callback.onError("Không thể chấp nhận yêu cầu kết bạn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void unfriend(String userId,String userIdMe ,ProfileCallback callback) {
        apiService.unFriend(userId,userIdMe).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(); // Cập nhật lại thông tin người dùng
                } else {
                    callback.onError("Không thể chấp nhận yêu cầu kết bạn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
