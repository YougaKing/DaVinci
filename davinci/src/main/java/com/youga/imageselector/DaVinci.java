package com.youga.imageselector;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by Youga on 2017/8/23.
 */


public class DaVinci {

    Configuration configuration = new Configuration();

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

    }


    public void openAlbum() {

    }


}
