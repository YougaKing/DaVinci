package com.youga.imageselector.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.youga.imageselector.entity.Folder;
import com.youga.imageselector.entity.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youga on 2016/1/7.
 */
public class ImageLoader implements LoaderManager.LoaderCallbacks<Cursor> {


    private boolean hasFolderGened;
    private ArrayList<Folder> mResultFolder = new ArrayList<>();
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private Context mContext;
    public static final int LOADER_ALL = 1, LOADER_CATEGORY = -1;
    CustomCallBack mCustomCallBack;

    public ImageLoader(Context context) {
        mContext = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ALL) {
            return new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
        } else if (id == LOADER_CATEGORY) {
            return new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        List<Image> images = new ArrayList<>();
        int count = data.getCount();
        if (count > 0) {
            data.moveToFirst();
            do {
                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                Image image = new Image(path, name, dateTime);
                images.add(image);

                if (!hasFolderGened) {
                    File imageFile = new File(path);
                    File folderFile = imageFile.getParentFile();
                    Folder folder = new Folder();
                    folder.name = folderFile.getName();
                    folder.path = folderFile.getAbsolutePath();
                    folder.cover = image;
                    if (!mResultFolder.contains(folder)) {
                        List<Image> imageList = new ArrayList<>();
                        imageList.add(image);
                        folder.images = imageList;
                        mResultFolder.add(folder);
                    } else {
                        Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                        f.images.add(image);
                    }
                }
            } while (data.moveToNext());

            mCustomCallBack.onLoadFinished(images, mResultFolder);
            hasFolderGened = true;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface CustomCallBack {
        void onLoadFinished(List<Image> images, ArrayList<Folder> resultFolder);
    }

    public void setCustomCallBack(CustomCallBack customCallBack) {
        this.mCustomCallBack = customCallBack;
    }
}
