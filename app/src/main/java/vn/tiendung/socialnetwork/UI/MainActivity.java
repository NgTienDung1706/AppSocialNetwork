package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.tiendung.socialnetwork.R;

public class MainActivity extends AppCompatActivity {

    private TextView txtHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nhận dữ liệu từ Intent
        String username = getIntent().getStringExtra("USERNAME");

        // Ánh xạ TextView và hiển thị "Hello, username"
        txtHello = findViewById(R.id.txtHello);
        txtHello.setText("Hello, " + username);
    }
}