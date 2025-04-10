package vn.tiendung.socialnetwork.API;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import vn.tiendung.socialnetwork.API.Response.ApiResponse;
import vn.tiendung.socialnetwork.Model.ChatItem;
import vn.tiendung.socialnetwork.Model.LoginResponse;
import vn.tiendung.socialnetwork.Model.MessageResponse;
import vn.tiendung.socialnetwork.Model.UserModel;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.Model.Message;
import vn.tiendung.socialnetwork.Model.ChatListResponse;

public interface APIService {
    @POST("/api/login")
    Call<ResponseBody> login(@Body UserModel user);

    @POST("/api/forgot-password")
    Call<Map<String, String>> sendOTP(@Body Map<String, String> body);

    @POST("/api/verifyOTP")
    Call<Map<String, String>> verifyOTP(@Body Map<String, String> body);

    @POST("/api/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> body);

    @POST("/api/register")
    Call<ResponseBody> registerUser(@Body UserModel user);

    @POST("/api/verifyAccount")
    Call<ResponseBody> validateOtp(@Body Map<String, String> requestBody);

    @GET("/api/getProfile/{userId}")
    Call<UserProfile> getUserProfile(@Path("userId") String userId);

    @Multipart
    @PUT("/api/update-profile")
    Call<MessageResponse> updateProfile(
            @Part("userId") RequestBody userId,
            @Part("fullname") RequestBody fullname,
            @Part("bio") RequestBody bio,
            @Part MultipartBody.Part file  // Ảnh đại diện (tuỳ chọn)
    );
    @GET("/api/chatlist/{userId}")
    Call<ChatListResponse> getChatList(@Path("userId") String userId);

    // Message APIs
    @GET("/api/message/{conversation_id}")
    Call<MessageResponse> getMessages(@Path("conversation_id") String conversationId);

    @POST("/api/message/send")
    Call<Message> sendMessage(@Body Message message);

    @Multipart
    @POST("/api/message/send-image")
    Call<ResponseBody> sendImageMessage(
            @Part MultipartBody.Part image,
            @Part("conversation_id") RequestBody conversationId,
            @Part("sender_id") RequestBody senderId
    );
}
