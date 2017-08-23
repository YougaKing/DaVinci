package com.youga.imageselector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.youga.imageselector.adapter.ImageGridAdapter;
import com.youga.imageselector.entity.Folder;
import com.youga.imageselector.entity.Image;
import com.youga.imageselector.loader.ImageLoader;
import com.youga.imageselector.popup.FolderPopupWindow;
import com.youga.imageselector.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youga on 2016/1/6.
 */
public class ImageFragment extends DialogFragment {

    public static final String TAG = "PhotoFragment";
    private ImageGridAdapter mImageAdapter;
    private View mPopupAnchorView;
    private TextView mTimeLineText;
    private Button mCategoryText, mPreviewBtn;
    private GridView mGridView;
    private ArrayList<Folder> mResultFolder;
    PhotoChoiceCallBack mCallBack;
    private FolderPopupWindow mFolderPopupWindow;
    private ImageLoader mImageLoader;
    private boolean mShowCamera;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (PhotoChoiceCallBack) activity;
    }

    public static ImageFragment newInstance(ArrayList<String> resultList, boolean showCamera, int number) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ResultList", resultList);
        bundle.putBoolean("ShowCamera", showCamera);
        bundle.putInt("Number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> resultList = getArguments().getStringArrayList("ResultList");
        if (resultList == null) resultList = new ArrayList<>();
        mShowCamera = getArguments().getBoolean("ShowCamera");
        int number = getArguments().getInt("Number");
        mImageAdapter = new ImageGridAdapter(getActivity(), number, mShowCamera, resultList);
        mPopupAnchorView = view.findViewById(R.id.footer);
        mPopupAnchorView.setVisibility(View.GONE);
        mTimeLineText = (TextView) view.findViewById(R.id.timeline_area);
        mTimeLineText.setVisibility(View.GONE);
        mCategoryText = (Button) view.findViewById(R.id.category_btn);
        mPreviewBtn = (Button) view.findViewById(R.id.preview);
        if (resultList.size() != 0) {
            mPreviewBtn.setEnabled(true);
            mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
        } else {
            mPreviewBtn.setEnabled(false);
        }
        mCategoryText.setText(R.string.folder_all);
        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mImageAdapter);
        mImageLoader = new ImageLoader(getActivity());
        mFolderPopupWindow = new FolderPopupWindow(getActivity());
        addListener();
    }

    private void addListener() {
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {

                if (state == SCROLL_STATE_IDLE) {
                    mTimeLineText.setVisibility(View.GONE);
                } else if (state == SCROLL_STATE_FLING) {
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTimeLineText.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                    Image image = (Image) view.getAdapter().getItem(index);
                    if (image != null) {
                        mTimeLineText.setText(Utils.formatPhotoDate(image.path));
                    }
                }
            }
        });

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {
                int gridWidth = mGridView.getWidth();
                int gridHeight = mGridView.getHeight();
                final int numCount = 3;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (gridWidth - columnSpace * (numCount - 1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        mImageAdapter.setItemOnClickListener(new ImageGridAdapter.ItemOnClickListener() {
            @Override
            public void onCameraItemClick() {
                mCallBack.shouldOpenCamera();
            }

            @Override
            public void onCheckBoxClick(ArrayList<String> resultList) {
                if (resultList.size() >= 0) {
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                } else {
                    mPreviewBtn.setEnabled(false);
                    mPreviewBtn.setText(R.string.preview);
                }
                mCallBack.onImageSelected(resultList);
            }

            @Override
            public void onSingleClick(Image data) {
                mCallBack.onSingleSelected(data.path);
            }
        });

        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showPopup(mResultFolder, mPopupAnchorView);
                }
            }
        });

        mImageLoader.setCustomCallBack(new ImageLoader.CustomCallBack() {
            @Override
            public void onLoadFinished(List<Image> images, ArrayList<Folder> resultFolder) {
                mResultFolder = resultFolder;
                mImageAdapter.setData(images);
                mPopupAnchorView.setVisibility(View.VISIBLE);
            }
        });

        mFolderPopupWindow.setFolderPopupWindowCallBack(new FolderPopupWindow.FolderPopupWindowCallBack() {
            @Override
            public void onItemClick(int position, Folder folder) {
                if (position == 0) {
                    getActivity().getSupportLoaderManager().restartLoader(ImageLoader.LOADER_ALL, null, mImageLoader);
                    mCategoryText.setText(R.string.folder_all);
                    mImageAdapter.setShowCamera(mShowCamera);
                } else {
                    mImageAdapter.setData(folder.images);
                    mCategoryText.setText(folder.name);
                    mImageAdapter.setShowCamera(false);
                }

                mGridView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(ImageLoader.LOADER_ALL, null, mImageLoader);
    }

    public interface PhotoChoiceCallBack {

        void onImageSelected(ArrayList<String> resultList);

        void shouldOpenCamera();

        void onSingleSelected(String path);
    }
}
