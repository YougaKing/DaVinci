package com.youga.imageselector.popup;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.youga.imageselector.R;
import com.youga.imageselector.adapter.FolderAdapter;
import com.youga.imageselector.entity.Folder;
import com.youga.imageselector.util.Utils;

import java.util.ArrayList;

/**
 * Created by Youga on 2016/1/7.
 */
public class FolderPopupWindow extends PopupWindow {

    private static final String TAG = "FolderPopupWindow";
    private FolderAdapter mFolderAdapter;
    FolderPopupWindowCallBack callBack;


    public FolderPopupWindow(Activity context) {
        super(context);
        mFolderAdapter = new FolderAdapter(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int actionBarSize = (int) context.getResources().getDimension(R.dimen.actionBarSize);
        Log.i(TAG, "actionBarSize:" + actionBarSize);
        int statusBarHeight = Utils.getStatusBarHeight(context);
        Log.i(TAG, "statusBarHeight:" + statusBarHeight);
        setWidth(metrics.widthPixels);
        setHeight(metrics.heightPixels - (actionBarSize * 3) - statusBarHeight);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = LayoutInflater.from(context).inflate(R.layout.popup_folder, null);
        setContentView(rootView);
        init(rootView);
    }

    private void init(View rootView) {
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mFolderAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                mFolderAdapter.setSelectIndex(position);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        Folder folder = (Folder) parent.getAdapter().getItem(position);
                        Log.i(TAG,"folder:"+folder);
                        callBack.onItemClick(position, folder);
                    }
                }, 100);
            }
        });
    }

    public void showPopup(ArrayList<Folder> mResultFolder, View anchor) {
        Log.i(TAG, "showPopup()");
        mFolderAdapter.setData(mResultFolder);
        showAsDropDown(anchor);
    }

    public void setFolderPopupWindowCallBack(FolderPopupWindowCallBack callBack) {
        this.callBack = callBack;
    }

    public interface FolderPopupWindowCallBack {
        void onItemClick(int position, Folder folder);
    }
}
