package com.youga.imageselector;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Youga on 2017/8/23.
 */

public class Configuration implements Parcelable {

    private Context context;
    private boolean crop;
    private int maxSize = 1;
    private boolean showCamera;
    private boolean showGif;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.crop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSize);
        dest.writeByte(this.showCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showGif ? (byte) 1 : (byte) 0);
    }

    public Configuration() {
    }

    protected Configuration(Parcel in) {
        this.crop = in.readByte() != 0;
        this.maxSize = in.readInt();
        this.showCamera = in.readByte() != 0;
        this.showGif = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}
