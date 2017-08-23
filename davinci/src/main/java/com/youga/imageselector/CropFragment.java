package com.youga.imageselector;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youga.imageselector.crop.CropImageView;
import com.youga.imageselector.util.Utils;

import java.io.File;

/**
 * Created by Youga on 2016/1/7.
 */
public class CropFragment extends Fragment {

    private static final String TAG = "CropFragment";
    private CropImageView cropImageView;

    public static CropFragment newInstance(Uri sourceUri) {
        CropFragment fragment = new CropFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("SourceUri", sourceUri);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        Uri mSourceUri = getArguments().getParcelable("SourceUri");
        cropImageView.setImageURI(mSourceUri);
    }

    public String getCroppedImagePath() {
        Bitmap croppedImage = cropImageView.getCroppedImage();
        try {
            String imageDir = Utils.createImageDir();
            File cropFile = new File(imageDir + "/" + "CropImage.jpg");
            Log.i(TAG, "cropFile:" + cropFile);
            Utils.writeBitmapFile(cropFile, croppedImage);
            return cropFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
