package vn.tiendung.socialnetwork.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.ViewModel.StoryViewModel;

public class StoryActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageButton btnCapture, btnGallery, btnSwitchCamera;
    private EditText etCaption;
    private ImageView imgCapturedPreview;

    private ImageCapture imageCapture;
    private StoryViewModel viewModel;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    viewModel.setImage(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        initViews();
        setupViewModel();
        observeData();
        assignFunction();
    }

    private void initViews() {
        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnGallery = findViewById(R.id.btnGallery);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        etCaption = findViewById(R.id.etCaption);
        imgCapturedPreview = findViewById(R.id.imgCapturedPreview);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(StoryViewModel.class);
    }

    private void observeData() {
        viewModel.getImageUri().observe(this, uri -> {
            if (uri != null) {
                imgCapturedPreview.setImageURI(uri);
                imgCapturedPreview.setVisibility(View.VISIBLE);
                etCaption.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.GONE);
                btnCapture.setImageResource(R.drawable.ic_send);
                btnSwitchCamera.setImageResource(R.drawable.ic_close);
            }
        });

        viewModel.isPreviewing().observe(this, preview -> {
            if (!preview) {
                imgCapturedPreview.setVisibility(View.GONE);
                etCaption.setVisibility(View.GONE);
                previewView.setVisibility(View.VISIBLE);
                etCaption.setText("");
                btnCapture.setImageResource(R.drawable.ic_camera);
                btnSwitchCamera.setImageResource(R.drawable.ic_switch_camera);
            }
        });
    }

    private void assignFunction() {
        startCamera();

        btnCapture.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.isPreviewing().getValue())) {
                viewModel.uploadStory(this, viewModel.getImageUri().getValue(), etCaption.getText().toString().trim());

                viewModel.getUploadResult().observe(this, resource -> {
                    switch (resource.getStatus()) {
                        case LOADING:
                            Toast.makeText(this, "Đang gửi story...", Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESS:
                            Toast.makeText(this, "Story đã được đăng!", Toast.LENGTH_SHORT).show();
                            viewModel.clearImage();
                            break;
                        case ERROR:
                            Toast.makeText(this, "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                });

            } else {
                capturePhoto();
            }
        });



        btnSwitchCamera.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.isPreviewing().getValue())) {
                viewModel.clearImage();
                clearOldTempPhotos();
            } else {
                viewModel.toggleCamera();
                startCamera();
            }
        });

        btnGallery.setOnClickListener(v -> openGallery());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = Boolean.TRUE.equals(viewModel.isBackCamera().getValue()) ?
                        CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("StoryActivity", "Camera binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capturePhoto() {
        if (imageCapture == null) return;

        String fileName = "capturedStory_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getCacheDir(), fileName);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(file);
                        viewModel.setImage(savedUri);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(StoryActivity.this, "Chụp ảnh thất bại!", Toast.LENGTH_SHORT).show();
                        Log.e("StoryActivity", "Capture failed: ", exception);
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void clearOldTempPhotos() {
        File cacheDir = getCacheDir();
        File[] files = cacheDir.listFiles((dir, name) -> name.startsWith("capturedStory_"));
        if (files != null) {
            for (File f : files) f.delete();
        }
    }
}
