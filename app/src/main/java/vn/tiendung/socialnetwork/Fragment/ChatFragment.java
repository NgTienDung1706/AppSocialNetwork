package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import vn.tiendung.socialnetwork.Adapter.ChatListAdapter;
import vn.tiendung.socialnetwork.Model.Chat;
import vn.tiendung.socialnetwork.R;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;

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

        // Gọi API lấy danh sách chat
        loadChatList();
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

    private void loadChatList() {
        chatList.add(new Chat("1", "Nguyễn Văn A", "Hello!", "10:30 AM", 2, true, "https://example.com/avatar1.jpg"));
        chatList.add(new Chat("2", "Trần Thị B", "Làm bài tập xong chưa?", "9:15 AM", 0, false, "https://example.com/avatar2.jpg"));
        chatList.add(new Chat("3", "Lê Văn C", "OK bạn ơi!", "8:00 AM", 5, true, "https://example.com/avatar3.jpg"));

        chatListAdapter = new ChatListAdapter(getContext(), chatList); // ✅ Khởi tạo lại adapter
        recyclerView.setAdapter(chatListAdapter); // ✅ Cập nhật RecyclerView

        chatListAdapter.notifyDataSetChanged();
    }

}

