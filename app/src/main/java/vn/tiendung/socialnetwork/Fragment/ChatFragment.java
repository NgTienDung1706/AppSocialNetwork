package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.Response.ApiResponse;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Adapter.ChatListAdapter;
import vn.tiendung.socialnetwork.Model.ChatItem;
import vn.tiendung.socialnetwork.Model.ChatListResponse;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.Utils.SocketManager;
import com.google.gson.Gson;
//import vn.tiendung.socialnetwork.API.Response.ChatListResponse;


public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<ChatItem> chatList;

    private List<String> currentOnlineIds = new ArrayList<>(); // Thêm biến lưu trữ danh sách user online


    private APIService apiService;

    private boolean isDataLoaded = false;

    Toolbar toolbar;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Toolbar toolbar = view.findViewById(R.id.chatToolbar);

        // Thiết lập Toolbar làm ActionBar của Activity
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        }

        // Khởi tạo danh sách chat
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getContext(), chatList);
        recyclerView.setAdapter(chatListAdapter);
        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        // Gọi API lấy danh sách chat
        loadChatList(userId);
        EditText searchChat = view.findViewById(R.id.searchChat);
        searchChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chatListAdapter.filter(s.toString()); // Hàm lọc tin nhắn
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giữ lại Fragment khi chuyển tab
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ChatFragment", "onResume called");

        // Yêu cầu trạng thái online mỗi khi fragment hiển thị
        if (SocketManager.getInstance().isConnected()) {
            SocketManager.getInstance().getSocket().emit("request_online_status");
        }
        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        loadChatList(userId);
        // Nếu chưa load dữ liệu, load lại
//        if (!isDataLoaded) {
//            String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
//            loadChatList(userId);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Không cần xóa listener ở đây vì MainActivity sẽ xử lý việc cập nhật
    }

    private void loadChatList(String userId) {
        if (userId.isEmpty()) {
            Log.e("ChatFragment", "Không tìm thấy userId trong SharedPreferences");
            return;
        }

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getChatList(userId).enqueue(new Callback<ChatListResponse>() {
            @Override
            public void onResponse(Call<ChatListResponse> call, Response<ChatListResponse> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    Log.d("ChatFragment", "API Response: " + new Gson().toJson(response.body()));
                    
                    chatList.clear();
                    List<ChatItem> chatItems = response.body().getChatList();

                    if (chatItems != null) {
                        chatList.addAll(chatItems);
                        isDataLoaded = true;
                        chatListAdapter.notifyDataSetChanged();

                        // Yêu cầu cập nhật trạng thái online sau khi load dữ liệu
                        if (SocketManager.getInstance().isConnected()) {
                            SocketManager.getInstance().getSocket().emit("request_online_status");
                        }

                        Log.d("ChatFragment", "Chat list loaded: " + chatList.size() + " items");
                    }
                } else {
                    Log.e("ChatFragment", "API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatListResponse> call, Throwable t) {
                Log.e("ChatFragment", "API Error: " + t.getMessage());
            }
        });
    }

    public void updateOnlineStatus(List<String> onlineUserIds) {
        // Phương thức cũ, giữ lại để tương thích
        refreshOnlineStatus(onlineUserIds);
    }

    public void refreshOnlineStatus(List<String> onlineUserIds) {
        // Lưu trữ danh sách hiện tại
        currentOnlineIds.clear();
        currentOnlineIds.addAll(onlineUserIds);

        Log.d("ChatFragment", "Refreshing online status, users online: " + onlineUserIds.size());

        // Nếu chưa load dữ liệu hoặc fragment chưa hiển thị, thoát
        if (!isDataLoaded || !isVisible() || getActivity() == null || chatList.isEmpty()) {
            Log.d("ChatFragment", "Skip update: dataLoaded=" + isDataLoaded +
                    ", isVisible=" + isVisible() + ", hasActivity=" + (getActivity() != null));
            return;
        }

        // Cập nhật UI trên thread chính
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < chatList.size(); i++) {
                ChatItem chatItem = chatList.get(i);
                String userId = chatItem.getUser().getId();
                boolean isOnline = onlineUserIds.contains(userId);

                if (chatItem.getUser().isOnline() != isOnline) {
                    Log.d("ChatFragment", "User " + userId + " online status changed to: " + isOnline);
                    chatItem.getUser().setOnline(isOnline);
                    chatListAdapter.notifyItemChanged(i);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái nếu cần
    }

}

