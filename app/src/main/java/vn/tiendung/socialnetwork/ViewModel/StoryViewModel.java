package vn.tiendung.socialnetwork.ViewModel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.tiendung.socialnetwork.Repository.StoryRepository;
import vn.tiendung.socialnetwork.Utils.Resource;


public class StoryViewModel extends ViewModel {

    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPreviewing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isBackCamera = new MutableLiveData<>(true);
    private final StoryRepository repository = new StoryRepository();
    private final MutableLiveData<Resource<Void>> uploadResult = new MutableLiveData<>();
    public LiveData<Resource<Void>> getUploadResult() {
        return uploadResult;
    }
    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    public LiveData<Boolean> isPreviewing() {
        return isPreviewing;
    }

    public LiveData<Boolean> isBackCamera() {
        return isBackCamera;
    }

    public void toggleCamera() {
        isBackCamera.setValue(!Boolean.TRUE.equals(isBackCamera.getValue()));
    }

    public void setImage(Uri uri) {
        imageUri.setValue(uri);
        isPreviewing.setValue(true);
    }

    public void clearImage() {
        imageUri.setValue(null);
        isPreviewing.setValue(false);
    }

    public void uploadStory(Context context, Uri imageUri, String caption) {
        repository.uploadStory(context, imageUri, caption).observeForever(result -> {
            uploadResult.postValue(result);
        });
    }
}



