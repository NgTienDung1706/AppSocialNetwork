package vn.tiendung.socialnetwork.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.Utils.Resource;

public class UserRepository {

    private final APIService apiService;

    public UserRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public LiveData<Resource<UserProfile>> getUserProfile(String userId) {
        MutableLiveData<Resource<UserProfile>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getUserProfile(userId).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error("Không tìm thấy người dùng", null));
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
