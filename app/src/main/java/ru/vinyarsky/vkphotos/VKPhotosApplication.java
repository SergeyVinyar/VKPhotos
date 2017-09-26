package ru.vinyarsky.vkphotos;

import android.app.Application;

import com.vk.sdk.VKSdk;

public final class VKPhotosApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
