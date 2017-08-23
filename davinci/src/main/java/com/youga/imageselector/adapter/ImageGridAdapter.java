package com.youga.imageselector.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youga.imageselector.R;
import com.youga.imageselector.entity.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * image Adapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private static final String TAG = "ImageGridAdapter";

    private Activity mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;

    private List<Image> mImages = new ArrayList<>();

    private int mItemSize;
    private AbsListView.LayoutParams mItemLayoutParams;
    private ItemOnClickListener mListener;
    ArrayList<String> mResultList;
    int mNumber;

    public ImageGridAdapter(Activity activity, int number, boolean showCamera, ArrayList<String> resultList) {
        mContext = activity;
        mResultList = resultList;
        mNumber = number;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) return;
        showCamera = b;
        notifyDataSetChanged();
    }

    /**
     * data list
     *
     * @param images
     */
    public void setData(List<Image> images) {

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {
        if (mItemSize == columnWidth) {
            return;
        }
        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if (type == TYPE_CAMERA) {
            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
            View cameraItem = view.findViewById(R.id.cameraItem);
            view.setTag(null);
            cameraItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCameraItemClick();
                }
            });
        } else if (type == TYPE_NORMAL) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                    holder = new ViewHolder(view);
                }
            }
            holder.bindData(getItem(i));

        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp.height != mItemSize) {
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    class ViewHolder {
        ImageView image;
        CheckBox checkBox;
        View mask;
        View view;

        ViewHolder(View view) {
            this.view = view;
            image = (ImageView) view.findViewById(R.id.image);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(final Image data) {
            if (data == null) return;
            // Single and Multiple Choice
            if (mNumber > 1) {
                checkBox.setVisibility(View.VISIBLE);
                if (mResultList.contains(data.path)) {
                    // choice state
                    checkBox.setChecked(true);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    // not choice
                    checkBox.setChecked(false);
                    mask.setVisibility(View.GONE);
                }
            } else {
                checkBox.setVisibility(View.GONE);
            }

            Uri uri = Uri.fromFile(new File(data.path));
            Glide.with(mContext).load(uri)
                    .placeholder(R.mipmap.default_error)
                    .override(mItemSize, mItemSize)
                    .centerCrop()
                    .into(image);

            if (mNumber > 1) {// Multiple Choice
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mResultList.contains(data.path)) {
                            mResultList.remove(data.path);
                            checkBox.setChecked(false);
                            mask.setVisibility(View.GONE);
                        } else {
                            // judge number
                            if (mNumber == mResultList.size()) {
                                checkBox.setChecked(!checkBox.isChecked());
                                String onlyChoiceMax = mContext.getResources().getString(R.string.only_choice_max);
                                String pageImage = mContext.getResources().getString(R.string.page_image);
                                Toast.makeText(mContext, onlyChoiceMax + mNumber + pageImage, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            checkBox.setChecked(true);
                            mask.setVisibility(View.VISIBLE);
                            mResultList.add(data.path);
                        }
                        mListener.onCheckBoxClick(mResultList);
                    }
                });
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onSingleClick(data);
                    }
                });
            }

        }
    }

    public void setItemOnClickListener(ItemOnClickListener listener) {
        this.mListener = listener;
    }

    public interface ItemOnClickListener {

        void onCameraItemClick();

        void onCheckBoxClick(ArrayList<String> resultList);

        void onSingleClick(Image data);
    }
}

