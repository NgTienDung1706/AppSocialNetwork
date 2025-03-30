package vn.tiendung.socialnetwork.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class OnlineStatusViewModel extends ViewModel {
    private MutableLiveData<List<String>> onlineUserIds = new MutableLiveData<>();

    public LiveData<List<String>> getOnlineUserIds() {
        return onlineUserIds;
    }

    public void setOnlineUserIds(List<String> userIds) {
        onlineUserIds.setValue(userIds);
    }
}
