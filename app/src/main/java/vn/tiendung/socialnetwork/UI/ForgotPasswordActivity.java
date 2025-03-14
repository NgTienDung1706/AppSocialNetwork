package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import vn.tiendung.socialnetwork.Fragment.ForgotPasswordFragment;
import vn.tiendung.socialnetwork.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // Load Fragment đầu tiên vào FragmentContainer
        if (savedInstanceState == null) {
            loadFragment(new ForgotPasswordFragment());
        }
    }

    // Phương thức để thay đổi Fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Cho phép quay lại Fragment trước đó khi bấm nút Back
                .commit();
    }
}
