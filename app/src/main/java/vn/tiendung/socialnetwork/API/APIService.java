package vn.tiendung.socialnetwork.API;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.Model.MessageResponse;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.PostRequest;
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
    @GET("/api/users/{userId}/profile/{userIdMe}")
    Call<UserProfile> getProfileUser(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );


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

    // Post API
    @GET("/api/post/{userId}")
    Call<List<Post>> getAllPosts(@Path("userId") String userId);

    @POST("/api/post/reaction/{postId}")
    Call<Void> addOrUpdateReaction(@Path("postId") String postId, @Body Map<String, String> body);

    @DELETE("/api/post/reaction/{postId}")
    Call<Void> deleteReaction(@Path("postId") String postId, @Query("userId") String userId);

    @GET("/api/post/myPosts/{userId}")
    Call<List<Post>> getMyPosts(@Path("userId") String userId);

    @GET("/api/post/postDetail")
    Call<Post> getPostById(
            @Query("postId") String postId,
            @Query("userId") String userId
    );
    //Story
    @POST("/api/post/story/create")
    Call<Post> createStory(@Body Post post);
    @GET("/api/post/story/{userId}")
    Call<List<Post>> getUserStories(@Path("userId") String userId);

    @POST("/api/post/create")
    Call<Void> createPost(@Body PostRequest postRequest);

    //Comments
    @GET("/api/posts/{postId}/comments")
    Call<List<Comment>> getCommentsByPostId(@Path("postId") String postId, @Query("userId") String userId);
    @POST("/api/posts/{postId}/comments")
    Call<Comment> createCommentByPostId(@Path("postId") String postId, @Body Comment comment);
    @PUT("/api/comments/{commentId}")
    Call<Void> updateCommentByCommentId(@Path("commentId") String commentId);
    @DELETE ("/api/comments/{commentId}")
    Call<Void> deleteCommentByCommentId(@Path("commentId") String commentId);
    @POST("/api/comments/{commentId}/like")
    Call<Void> likeCommentByCommentId(@Path("commentId") String commentId, @Query("userId") String userId);
    @DELETE("/api/comments/{commentId}/like")
    Call<Void> unlikeCommentByCommentId(@Path("commentId") String commentId, @Query("userId") String userId);


    @GET("/api/search/user")
    Call<List<Friend>> searchFriends(@Query("keyword") String keyword,
                                     @Query("userId") String userId);

    @GET("/api/post/search")
    Call<List<Post>> searchPosts(@Query("keyword") String keyword,
                                     @Query("userId") String userId);


    @POST("/api/users/{userId}/addFriend/{userIdMe}")
    Call<Void> addFriend(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );

    @PUT("/api/users/{userId}/acceptFriend/{userIdMe}")
    Call<Void> acceptFriend(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );

    @PUT("/api/users/{userId}/rejectFriend/{userIdMe}")
    Call<Void> rejectFriend(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );

    @DELETE("/api/users/{userId}/unFriend/{userIdMe}")
    Call<Void> unFriend(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );


    @PUT("/api/users/{userId}/cancelFriendRequest/{userIdMe}")
    Call<Void> cancelFriendRequest(
            @Path("userId") String userId,
            @Path("userIdMe") String userIdMe
    );

    @GET("/api/friendrequests/{userId}")
    Call<List<Friend>> getFriendRequests(@Path("userId") String userId);


    @GET("/api/friends/{userId}")
    Call<List<Friend>> getFriends(@Path("userId") String userId);

    @POST("/api/conversations/private")
    Call<Map<String, String>> createOrGetPrivateConversation(
            @Query("userId") String userId,
            @Query("userIdMe") String userIdMe
    );

    @GET("/api/friendsuggested/{userId}")
    Call<List<Friend>> getSuggestedFriends(@Path("userId") String userId);

}
