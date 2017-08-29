package com.youga.imageselector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.youga.imageselector.util.StorageUtils;

/**
 * Created by Youga on 2017/8/23.
 */


public class DaVinci {

    private Configuration configuration = new Configuration();

    private DaVinci() {
    }

    public static DaVinci with(@NonNull Context context) {
        DaVinci instance = new DaVinci();
        instance.configuration.setContext(context.getApplicationContext());
        return instance;
    }

    public void showCamera() {
        configuration.setShowCamera(true);
    }

    public void showGif() {
        configuration.setShowGif(true);
    }

    public void radio(boolean crop) {
        configuration.setMaxSize(1);
        configuration.setCrop(crop);
    }

    public void multiple(@IntRange(from = 2) int maxSize) {
        configuration.setCrop(false);
        configuration.setMaxSize(maxSize);
    }

    public void openCamera() {
        Intent intent = new Intent(context, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaActivity.EXTRA_CONFIGURATION, configuration);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    public void openAlbum() {
        Intent intent = new Intent(context, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaActivity.EXTRA_CONFIGURATION, configuration);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 执行
     */
    private void execute() {
        Context context = configuration.getContext();
        if (context == null) {
            return;
        }
        if (!StorageUtils.existSDcard()) {
            Toast.makeText(context, "没有找到SD卡", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, VinciActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(VinciActivity.EXTRA_CONFIGURATION, configuration);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
