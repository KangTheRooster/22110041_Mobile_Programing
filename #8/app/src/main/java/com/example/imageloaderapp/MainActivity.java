package com.example.imageloaderapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    private static final int LOADER_ID = 100;
    private EditText urlEditText;
    private Button loadImageButton;
    private TextView statusTextView;
    private ImageView imageView;
    private NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Xin quyền POST_NOTIFICATIONS nếu đang chạy Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        urlEditText = findViewById(R.id.urlEditText);
        loadImageButton = findViewById(R.id.loadImageButton);
        statusTextView = findViewById(R.id.statusTextView);
        imageView = findViewById(R.id.imageView);

        // Service chạy nền
        startService(new Intent(this, ImageLoaderService.class));

        // BroadcastReceiver mạng
        receiver = new NetworkChangeReceiver(loadImageButton, statusTextView);
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        loadImageButton.setOnClickListener(view -> {
            String url = urlEditText.getText().toString();
            if (url.isEmpty()) {
                statusTextView.setText("URL is empty");
                return;
            }
            statusTextView.setText("Loading...");
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this).forceLoad();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.w("Permission", "POST_NOTIFICATIONS permission denied");
            }
        }
    }

    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
        return new ImageLoaderAsyncTaskLoader(this, args.getString("url"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
        if (data != null) {
            imageView.setImageBitmap(data);
            statusTextView.setText("Image loaded successfully");
        } else {
            statusTextView.setText("Failed to load image");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
