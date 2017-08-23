package com.youga.imageselector.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youga.imageselector.R;
import com.youga.imageselector.entity.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseAdapter {

    private final String page,allImage;
    private Activity mContext;
    private LayoutInflater mInflater;

    private List<Folder> mFolders = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;

    public FolderAdapter(Activity context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_cover_size);
        page = context.getResources().getString(R.string.page);
        allImage = context.getResources().getString(R.string.all_image);
    }

    public void setData(List<Folder> folders) {
        if (folders != null && folders.size() > 0) {
            mFolders = folders;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public Folder getItem(int i) {
        if (i == 0) return null;
        return mFolders.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText(allImage);
                holder.size.setText(getTotalImageSize() + page);
                if (mFolders.size() > 0) {
                    Folder f = mFolders.get(0);

                    Uri uri = Uri.fromFile(new File(f.cover.path));
                    Glide.with(mContext).load(uri)
                            .placeholder(R.mipmap.default_error)
                            .override(mImageSize,mImageSize)
                            .centerCrop()
                            .into(holder.cover);
                }
            } else {
                holder.bindData(getItem(i));
            }
            if (lastSelected == i) {
                holder.radioButton.setChecked(true);
                holder.radioButton.setVisibility(View.VISIBLE);
            } else {
                holder.radioButton.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (Folder f : mFolders) {
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) return;
        lastSelected = i;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;
        RadioButton radioButton;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            size = (TextView) view.findViewById(R.id.size);
            radioButton = (RadioButton) view.findViewById(R.id.radioButton);
            view.setTag(this);
        }

        void bindData(Folder folder) {
            name.setText(folder.name);
            size.setText(folder.images.size() + page);


            Uri uri = Uri.fromFile(new File(folder.cover.path));
            Glide.with(mContext).load(uri)
                    .placeholder(R.mipmap.default_error)
                    .override(mImageSize,mImageSize)
                    .centerCrop()
                    .into(cover);
        }
    }

}
