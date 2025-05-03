package vn.tiendung.socialnetwork.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<List<Friend>> friendList;

    private MutableLiveData<List<Post>> postList;
    private APIService apiService;

    private SharedPrefManager sharedPrefManager;

    public UserViewModel(Application application) {
        super(application);
        friendList = new MutableLiveData<>();
        postList = new MutableLiveData<>();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        sharedPrefManager = SharedPrefManager.getInstance(application.getApplicationContext()); // Đảm bảo lấy context
    }

    // Method gọi API để tìm kiếm bạn bè theo keyword
    public void searchFriends(String keyword) {
        String userId = sharedPrefManager.getUserId();
        if (userId == null || keyword == null || keyword.isEmpty()) {
            return; // Nếu không có userId hoặc keyword, không thực hiện tìm kiếm
        }

        // Giả sử API yêu cầu thêm userId trong tham số tìm kiếm
        Call<List<Friend>> call = apiService.searchFriends(keyword,userId);
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                // Xử lý khi gọi API thất bại
            }
        });
    }

    public void searchPosts(String keyword) {
        Log.d("UserViewModel", "searchFriends called with keyword: " + keyword);
        String userId = sharedPrefManager.getUserId();  // Lấy userId từ SharedPreferences
        Log.d("UserViewModel", "userId: " + userId);
        if (userId == null || keyword == null || keyword.isEmpty()) {
            return; // Nếu không có userId hoặc keyword, không thực hiện tìm kiếm
        }

        // Giả sử API yêu cầu thêm userId trong tham số tìm kiếm
        Call<List<Post>> call = apiService.searchPosts(keyword,userId);
        Log.d("UserViewModel", "Before enqueue");
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserViewModel", "API returned " + response.body().size() + " friends");
                    postList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Xử lý khi gọi API thất bại
            }
        });
        Log.d("UserViewModel", "After enqueue");
    }

    public LiveData<List<Friend>> getFriendList() {
        return friendList;
    }
    public LiveData<List<Post>> getPostList() {
        return postList;
    }
}
