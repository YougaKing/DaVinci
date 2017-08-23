package com.youga.imageselector.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public class Utils {

    public static String timeFormat(long timeMillis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time) {
        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path) {
        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    public static String createImageDir() {
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        File imageDir = new File(rootPath + "/" + "YougaLibrary");
        if (!imageDir.exists()) imageDir.mkdirs();
        return imageDir.getAbsolutePath();
    }

    public static File createImageFile() {
        String imageDir = createImageDir();
        File imageFile = new File(imageDir + "/" + "CameraImage.jpg");
        imageFile.deleteOnExit();
        return imageFile;
    }

    public static String getImageFilePath() {
        String imageDir = createImageDir();
        return new File(imageDir + "/" + "CameraImage.jpg").getAbsolutePath();
    }

    public static File writeBitmapFile(File file, Bitmap bitmap) throws IOException {
        FileOutputStream bmpFile = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile);
        bmpFile.flush();
        bmpFile.close();
        return file;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static Bitmap getBitmapURI(Context context, Uri uri) {
        InputStream is = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        try {
            is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, option);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeSilently(is);
        }
    }

    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public static void startApplicationDetails(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static String getApplicationName(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Android Application";
        }
    }
}
