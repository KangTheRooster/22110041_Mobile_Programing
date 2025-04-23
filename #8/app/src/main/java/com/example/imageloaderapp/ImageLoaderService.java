package com.example.imageloaderapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.Log;
import android.Manifest;

public class ImageLoaderService extends Service {
    private Handler handler;
    private Runnable notificationRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                showNotification();
                handler.postDelayed(this, 5 * 60 * 1000); // 5 phút
            }
        };
        handler.post(notificationRunnable);
        return START_STICKY;
    }

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Image Loader")
                .setContentText("Image Loader Service is running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        //từ Android 13 (API 33) trở lên, nếu bạn muốn gửi thông báo (notification),
        // bạn bắt buộc phải xin quyền POST_NOTIFICATIONS từ người dùng lúc chạy ứng dụng.
        // Việc chỉ thêm trong AndroidManifest.xml là chưa đủ.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());

        } else {
            Log.w("ImageLoaderService", "POST_NOTIFICATIONS permission not granted.");
            // Có thể gửi broadcast để yêu cầu Activity xin quyền
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(notificationRunnable);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
