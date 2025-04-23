package com.example.imageloaderapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoaderAsyncTaskLoader extends AsyncTaskLoader<Bitmap> {
    private final String url;

    public ImageLoaderAsyncTaskLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Nullable
    @Override
    public Bitmap loadInBackground() {
        try {
            InputStream input = new URL(url).openStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }
}

