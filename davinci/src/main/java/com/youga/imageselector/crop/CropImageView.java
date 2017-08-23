/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.youga.imageselector.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


/**
 * Custom view that provides cropping capabilities to an image.
 */
public class CropImageView extends RelativeLayout {

    private static final String TAG = "CropImageView";
    private int mScreenWidth, mScreenHeight;
    private Bitmap mBitmap;
    private Context mContext;

    private CropZoomView mZoomView;
    private CropBorderView mBorderView;


    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        mZoomView = new CropZoomView(context);
        mBorderView = new CropBorderView(context);

        Log.i(TAG, "mScreenWidth:" + mScreenWidth + "--mScreenHeight:" + mScreenHeight);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(mZoomView, lp);
        this.addView(mBorderView, lp);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mZoomView.setImageBitmap(mBitmap);
    }

    public void setImageBitmap(Bitmap bitmap, ExifInterface exif) {

        if (bitmap == null) {
            return;
        }
        if (exif == null) {
            setImageBitmap(bitmap);
            return;
        }

        final Matrix matrix = new Matrix();
        final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotate = -1;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        if (rotate == -1 && bitmap.getWidth() > bitmap.getHeight()) rotate = 90;
        if (rotate == -1) {
            setImageBitmap(bitmap);
        } else {
            matrix.postRotate(rotate);
            final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);
            setImageBitmap(rotatedBitmap);
            bitmap.recycle();
        }
    }

    public Bitmap getCroppedImage() {
        return mZoomView.clip();
    }

    public void setImageURI(Uri uri) {
        InputStream is = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            option.inSampleSize = calculateBitmapSampleSize(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, option);
            ExifInterface exif = new ExifInterface(uri.getPath());
            setImageBitmap(bitmap, exif);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = mContext.getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options);
        } finally {
            closeSilently(is);
        }
        int sampleSize;
        if (options.outWidth > options.outHeight) {
            sampleSize = options.outHeight / mScreenWidth;
        } else {
            sampleSize = options.outWidth / mScreenWidth;
        }
        return sampleSize;
    }

    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
