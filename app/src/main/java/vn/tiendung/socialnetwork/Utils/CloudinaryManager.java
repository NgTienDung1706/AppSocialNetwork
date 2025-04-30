package vn.tiendung.socialnetwork.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class CloudinaryManager {
    private static final String CLOUDINARY_CLOUD_NAME="dfinxo4uj";
    private static final String CLOUDINARY_API_KEY="155328539331751";
    private static final String CLOUDINARY_API_SECRET="mW-mk-8oPeol9mhwi5rMNr1_JcE";
    private static CloudinaryManager instance;
    private final Cloudinary cloudinary;

    private CloudinaryManager() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUDINARY_CLOUD_NAME,
                "api_key", CLOUDINARY_API_KEY,
                "api_secret", CLOUDINARY_API_SECRET
        ));
    }

    public static CloudinaryManager getInstance() {
        if (instance == null) {
            instance = new CloudinaryManager();
        }
        return instance;
    }

    public LiveData<Resource<String>> uploadImage(Context context, Uri imageUri) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        new Thread(() -> {
            try {
                Log.d("CLOUDINARY", "URI nhận được: " + imageUri.toString());

                File file = createTempFileFromUri(context, imageUri);
                Log.d("CLOUDINARY", "Đường dẫn file tạm: " + file.getAbsolutePath());

                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");

                Log.d("CLOUDINARY", "Upload thành công, URL: " + url);

                result.postValue(Resource.success(url));
            } catch (Exception e) {
                Log.e("CLOUDINARY", "Lỗi upload ảnh: " + e.getMessage());
                result.postValue(Resource.error("Lỗi upload: " + e.getMessage(), null));
            }
        }).start();

        return result;
    }


    private File createTempFileFromUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload_", ".jpg", context.getCacheDir());

        OutputStream outputStream = new java.io.FileOutputStream(tempFile);

        if (inputStream != null && outputStream != null) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
        }
        return tempFile;
    }
}