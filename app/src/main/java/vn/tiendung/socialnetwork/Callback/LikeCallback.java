package vn.tiendung.socialnetwork.Callback;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeCallback implements Callback<Void> {

    private final String commentId;

    public LikeCallback(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            Log.d("LikeCallback", "Like thành công cho comment: " + commentId);
        } else {
            Log.e("LikeCallback", "Lỗi response: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Log.e("LikeCallback", "Lỗi kết nối khi like comment " + commentId + ": " + t.getMessage());
    }
}
