package vn.tiendung.socialnetwork.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.MessageResponse;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {
    private static final int REQUEST_PERMISSION_CODE = 100;

    private ImageView imgAvatar;
    private TextView tvUsername;
    private EditText edtName, edtBio;
    private Button btnSave;

    private Uri selectedImageUri; // Lưu ảnh được chọn


    private String avatarUrl, fullname, bio, username; // Thêm username

    public static EditProfileBottomSheet newInstance(String avatarUrl, String fullname, String bio, String username) {
        EditProfileBottomSheet fragment = new EditProfileBottomSheet();
        Bundle args = new Bundle();
        args.putString("avatarUrl", avatarUrl);
        args.putString("fullname", fullname);
        args.putString("bio", bio);
        args.putString("username", username); // Truyền username
        fragment.setArguments(args);
        return fragment;
    }
    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }

    private OnProfileUpdatedListener listener;

    public void setOnProfileUpdatedListener(OnProfileUpdatedListener listener) {
        this.listener = listener;
    }

    private void updateProfileSuccess() {
        if (listener != null) {
            Log.d("PROFILE_UPDATE", "Đã gọi onProfileUpdated()"); // Kiểm tra xem hàm có được gọi không
            listener.onProfileUpdated();
        }
        dismiss(); // Đóng BottomSheet sau khi cập nhật
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvUsername = view.findViewById(R.id.tVUserName);
        edtName = view.findViewById(R.id.edtName);
        edtBio = view.findViewById(R.id.edtBio);
        btnSave = view.findViewById(R.id.btnSave);

        if (getArguments() != null) {
            avatarUrl = getArguments().getString("avatarUrl");
            fullname = getArguments().getString("fullname");
            bio = getArguments().getString("bio");
            username = getArguments().getString("username"); // Lấy username từ Bundle

            Glide.with(requireContext()).load(avatarUrl).placeholder(R.drawable.avt_default).into(imgAvatar);
            edtName.setText(fullname);
            edtBio.setText(bio);
            tvUsername.setText(username);
        }

        btnSave.setOnClickListener(v -> {
            updateProfile();
        });

        imgAvatar.setOnClickListener(v -> openGallery());

        return view;
    }
    private void openGallery() {
        checkAndRequestPermission();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        // Hiển thị ảnh đã chọn lên ImageView
                        Glide.with(requireContext()).load(selectedImageUri).into(imgAvatar);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi khi chọn ảnh!", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", "Lỗi chọn ảnh: " + e.getMessage());
                    }
                }
            });
    private void updateProfile() {
        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        String fullname = edtName.getText().toString();
        String bio = edtBio.getText().toString();

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);;

        // Chuyển đổi dữ liệu
        RequestBody userIdPart = RequestBody.Companion.create(userId, MediaType.parse("text/plain"));
        RequestBody fullnamePart = RequestBody.Companion.create(fullname, MediaType.parse("text/plain"));
        RequestBody bioPart = RequestBody.Companion.create(bio, MediaType.parse("text/plain"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Chưa có quyền đọc ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        MultipartBody.Part filePart  = null;
        if (selectedImageUri != null) {
            //File file = new File(FileUtils.getPath(getContext(), selectedImageUri));
            File file = new File(getRealPathFromURI(selectedImageUri));
            RequestBody requestFile = RequestBody.Companion.create(file, MediaType.parse("image/*"));
            filePart  = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }

        // Gửi request
        Call<MessageResponse> call = apiService.updateProfile(userIdPart, fullnamePart, bioPart, filePart );
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    updateProfileSuccess();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối: " + t.getMessage());
                Log.e("API_ERROR", "Lỗi kết nối: " + t.getMessage());
                t.printStackTrace(); // In lỗi chi tiết

                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getRealPathFromURI(Uri uri) {
        String result = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx != -1) {
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(requireContext(), "Quyền đã được cấp!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Bạn cần cấp quyền để chọn ảnh!", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else { // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }


}
