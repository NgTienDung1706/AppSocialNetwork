package vn.tiendung.socialnetwork.API;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.tiendung.socialnetwork.Model.LoginResponse;
import vn.tiendung.socialnetwork.Model.UserModel;

public interface APIService {
    @POST("/api/login")
    Call<LoginResponse> login(@Body UserModel user);

    @POST("/api/forgot-password")
    Call<Map<String, String>> sendOTP(@Body Map<String, String> body);

    @POST("/api/verifyOTP")
    Call<Map<String, String>> verifyOTP(@Body Map<String, String> body);

    @POST("/api/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> body);
}
