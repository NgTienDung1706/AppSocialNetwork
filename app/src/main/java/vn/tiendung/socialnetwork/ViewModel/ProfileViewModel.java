package vn.tiendung.socialnetwork.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.Repository.ProfileRepository;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
public class ProfileViewModel extends ViewModel {
    private ProfileRepository profileRepository;
    private MutableLiveData<UserProfile> userProfileLiveData;
    private MutableLiveData<String> errorMessageLiveData;
    private MutableLiveData<String> friendStatusLiveData; // Lưu trạng thái kết bạn

    public ProfileViewModel() {
        profileRepository = new ProfileRepository();
        userProfileLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
        friendStatusLiveData = new MutableLiveData<>();
        //String userIdMe = SharedPrefManager.getInstance(context).getUserId();

    }

    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<String> getFriendStatusLiveData() {
        return friendStatusLiveData;
    }

    public void fetchUserProfile(String userId,String userIdMe) {

        profileRepository.getUserProfile(userId,userIdMe, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                userProfileLiveData.setValue(userProfile);
                friendStatusLiveData.setValue(userProfile.getRelationship());
            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage);
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    public void addFriend(String userId,String userIdMe) {
        profileRepository.addFriend(userId,userIdMe,new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess() {
                friendStatusLiveData.setValue("received"); // Cập nhật trạng thái kết bạn
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage); // Hiển thị lỗi
            }
        });
    }
    public void accept(String userId,String userIdMe) {
        profileRepository.accept(userId,userIdMe,new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess() {
                friendStatusLiveData.setValue("friend"); // Cập nhật trạng thái kết bạn
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage); // Hiển thị lỗi
            }
        });
    }
    public void reject(String userId,String userIdMe) {
        profileRepository.reject(userId,userIdMe,new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess() {
                friendStatusLiveData.setValue("none"); // Cập nhật trạng thái kết bạn
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage); // Hiển thị lỗi
            }
        });
    }
    public void unfriend(String userId,String userIdMe) {
        profileRepository.unfriend(userId,userIdMe,new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess() {
                friendStatusLiveData.setValue("none"); // Cập nhật trạng thái kết bạn
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage); // Hiển thị lỗi
            }
        });
    }
}
