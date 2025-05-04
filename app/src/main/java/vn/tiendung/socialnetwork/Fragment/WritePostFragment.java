package vn.tiendung.socialnetwork.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.SelectedImagesAdapter;
import vn.tiendung.socialnetwork.Model.PostRequest;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.CloudinaryManager;
import vn.tiendung.socialnetwork.Utils.Resource;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.ViewModel.PostViewModel;

public class WritePostFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 1;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private SelectedImagesAdapter adapter;
    private RecyclerView recyclerSelectedImages;
    private EditText edtContent;
    private Button btnPickImage, btnPost;

    private EditText edtHashtags;

    private ProgressBar progressBar;

    private PostViewModel postViewModel;


    public WritePostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_writepost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        edtContent = view.findViewById(R.id.edtContent);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        btnPost = view.findViewById(R.id.btnPost);
        edtHashtags = view.findViewById(R.id.edtHashtags);
        progressBar = view.findViewById(R.id.progressBar);

        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);


        recyclerSelectedImages = view.findViewById(R.id.recyclerSelectedImages);

        adapter = new SelectedImagesAdapter(selectedImageUris, uri -> {
            selectedImageUris.remove(uri);
            adapter.notifyDataSetChanged();
            toggleRecyclerVisibility();
        });

        recyclerSelectedImages.setAdapter(adapter);
        recyclerSelectedImages.setLayoutManager(new GridLayoutManager(getContext(), 3));

        btnPickImage.setOnClickListener(v -> openGallery());
        btnPost.setOnClickListener(v -> postContent());
        observeViewModel();
    }
    private void observeViewModel() {
        postViewModel.getPostStatus().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                    edtContent.setText("");
                    edtHashtags.setText("");
                    selectedImageUris.clear();
                    adapter.notifyDataSetChanged();
                    toggleRecyclerVisibility();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGES_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (!selectedImageUris.contains(imageUri)) {
                            selectedImageUris.add(imageUri);
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    if (!selectedImageUris.contains(imageUri)) {
                        selectedImageUris.add(imageUri);
                    }
                }
                adapter.notifyDataSetChanged();
                toggleRecyclerVisibility();
            }
        }
    }

    private void toggleRecyclerVisibility() {
        recyclerSelectedImages.setVisibility(selectedImageUris.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void postContent() {
        String caption = edtContent.getText().toString().trim();
        String hashtagInput = edtHashtags.getText().toString().trim();
        List<String> hashtags = extractHashtags(hashtagInput);

        if (caption.isEmpty() && selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập nội dung hoặc chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        List<String> uploadedUrls = new ArrayList<>();
        uploadImagesRecursive(0, uploadedUrls, () -> {
            progressBar.setVisibility(View.GONE);

            PostRequest.Content content = new PostRequest.Content(caption, hashtags, uploadedUrls);
            PostRequest request = new PostRequest();

            String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
            request.setUserid(userId); // Lấy từ SharedPreferences
            request.setContent(content);

            postViewModel.createPost(request);
        });
    }


    private List<String> extractHashtags(String input) {
        List<String> tags = new ArrayList<>();
        for (String word : input.split("\\s+")) {
            if (word.startsWith("#")) {
                tags.add(word.replace("#", ""));
            }
        }
        return tags;
    }

    private void uploadImagesRecursive(int index, List<String> uploadedUrls, Runnable onComplete) {
        if (index >= selectedImageUris.size()) {
            onComplete.run();
            return;
        }

        Uri imageUri = selectedImageUris.get(index);
        CloudinaryManager.getInstance().uploadImage(requireContext(), imageUri)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.getStatus() == Resource.Status.SUCCESS) {
                        uploadedUrls.add(resource.getData());
                        uploadImagesRecursive(index + 1, uploadedUrls, onComplete);
                    } else if (resource.getStatus() == Resource.Status.ERROR) {
                        Toast.makeText(getContext(), "Upload thất bại: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }



}

