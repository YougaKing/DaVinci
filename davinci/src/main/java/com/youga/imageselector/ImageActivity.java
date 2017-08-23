package com.youga.imageselector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youga.imageselector.util.Utils;

import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Youga on 2016/2/16.
 */
public class ImageActivity extends FragmentActivity implements ImageFragment.PhotoChoiceCallBack {

    private static final String TAG = "ImageActivity";
    private static final String KEY_RESTART = "KEY_RESTART";
    TextView toolbar;
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    private static final String ACTION_CAMERA = "ACTION_CAMERA", ACTION_IMAGE = "ACTION_IMAGE";
    private static final String[] PARAMS = {"Action", "CropImage", "Number", "ShowCamera", "ResultList"};
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 254;
    private static final int REQ_CAMERA = 33;
    private Button mBtnComplete;
    private ArrayList<String> mResultList;
    private boolean mRestart;

    public static Intent openCamera(Context context, boolean crop) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ImageActivity.PARAMS[0], ImageActivity.ACTION_CAMERA);
        intent.putExtra(ImageActivity.PARAMS[1], crop);
        return intent;
    }

    public static Intent choiceImage(Context context, int number, ArrayList<String> resultList) {
        return choiceImage(context, false, number, false, resultList);
    }

    public static Intent choiceImage(Context context, int number, boolean showCamera, ArrayList<String> resultList) {
        return choiceImage(context, false, number, showCamera, resultList);
    }

    public static Intent choiceImage(Context context, boolean crop, int number,
                                     boolean showCamera, ArrayList<String> resultList) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ImageActivity.PARAMS[0], ImageActivity.ACTION_IMAGE);
        intent.putExtra(ImageActivity.PARAMS[1], crop);
        intent.putExtra(ImageActivity.PARAMS[2], number);
        intent.putExtra(ImageActivity.PARAMS[3], showCamera);
        intent.putExtra(ImageActivity.PARAMS[4], resultList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initToolBar();
        if (!mayWriteExternalStorage()) return;
        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_RESTART)) {
            mRestart = savedInstanceState.getBoolean(KEY_RESTART);
            Log.i(TAG, "onCreate()-->RESTART");
        } else {
            Log.i(TAG, "onCreate()");
        }
        init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RESTART, true);
    }

    private void initToolBar() {
        toolbar = (TextView) findViewById(R.id.toolbar);
        ImageButton back = (ImageButton) findViewById(R.id.back);
        toolbar.setText(getString(R.string.image));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mBtnComplete = (Button) findViewById(R.id.btn_complete);
        String action = getIntent().getStringExtra(PARAMS[0]);
        if (action == null) throw new RuntimeException("Missing parameter Action");
        switch (action) {
            case ACTION_CAMERA:
                if (mRestart) return;
                openCamera();
                break;
            case ACTION_IMAGE:
                if (mRestart) return;
                boolean showCamera = getIntent().getBooleanExtra(PARAMS[3], false);
                int number = getIntent().getIntExtra(PARAMS[2], 1);
                mResultList = getIntent().getStringArrayListExtra(PARAMS[4]);
                ImageFragment imageFragment = ImageFragment.newInstance(mResultList, showCamera, number);
                showHierarchyFragment(imageFragment, R.id.container);

                mBtnComplete.setVisibility(View.VISIBLE);
                if (number > 1) {
                    if (mResultList.size() > 0) {
                        mBtnComplete.setText("完成(" + mResultList.size() + "/" + number + ")");
                        mBtnComplete.setEnabled(true);
                    } else {
                        mBtnComplete.setText("完成");
                        mBtnComplete.setEnabled(false);
                    }
                } else {
                    mBtnComplete.setVisibility(View.INVISIBLE);
                }

                mBtnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data = new Intent();
                        data.putStringArrayListExtra(EXTRA_RESULT, mResultList);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                break;
        }

    }

    public boolean mayWriteExternalStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults == null) return;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    String permissionApply = getString(R.string.permission_apply);
                    String settingApp = getString(R.string.setting_application);
                    String openSDCardPermission = getString(R.string.open_sdcard_permission);
                    String goSetting = getString(R.string.go_setting);
                    String function = getString(R.string.function);
                    String cancel = getString(R.string.cancel);

                    showAlertDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Utils.startApplicationDetails(ImageActivity.this);
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    finish();
                                    break;
                            }
                        }
                    }, permissionApply, settingApp + Utils.getApplicationName(ImageActivity.this)
                            + openSDCardPermission + Utils.getApplicationName(ImageActivity.this)
                            + goSetting, function, cancel);
                }
                break;
        }
    }


    @Override
    public void onImageSelected(ArrayList<String> resultList) {
        mResultList = resultList;
        int number = getIntent().getIntExtra(PARAMS[2], 1);
        boolean crop = getIntent().getBooleanExtra(ImageActivity.PARAMS[1], false);
        if (crop && number == 1 && resultList.size() > 0) {
            whetherCrop(resultList.get(0), true);
        } else {
            mBtnComplete.setVisibility(View.VISIBLE);
            if (mResultList.size() > 0) {
                mBtnComplete.setText("完成(" + mResultList.size() + "/" + number + ")");
                mBtnComplete.setEnabled(true);
            } else {
                mBtnComplete.setText("完成");
                mBtnComplete.setEnabled(false);
            }
        }
    }

    @Override
    public void shouldOpenCamera() {
        openCamera();
    }

    @Override
    public void onSingleSelected(String filePath) {
        if (filePath == null) {
            finish();
            return;
        }
        String action = getIntent().getStringExtra(PARAMS[0]);
        boolean crop = getIntent().getBooleanExtra(ImageActivity.PARAMS[1], false);
        switch (action) {
            case ACTION_CAMERA:
                whetherCrop(filePath, crop);
                break;
            case ACTION_IMAGE:
                int number = getIntent().getIntExtra(PARAMS[2], 1);
                whetherCrop(filePath, crop && number == 1);
                break;
        }
    }

    private void whetherCrop(String filePath, boolean crop) {
        if (crop) {
            CropFragment cropFragment = CropFragment.newInstance(Uri.fromFile(new File(filePath)));
            showHierarchyFragment(cropFragment, R.id.container);

            mBtnComplete.setVisibility(View.VISIBLE);
            mBtnComplete.setText("完成");
            mBtnComplete.setEnabled(true);

            mBtnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    ArrayList<String> resultList = new ArrayList<>();
                    resultList.add(((CropFragment) fragment).getCroppedImagePath());
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        } else {
            ArrayList<String> resultList = new ArrayList<>();
            resultList.add(filePath);
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }


    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.createImageFile()));
            startActivityForResult(intent, REQ_CAMERA);
        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    onSingleSelected(Utils.getImageFilePath());
                } else {
                    onSingleSelected(null);
                }
                break;
        }
    }

    public void showAlertDialog(DialogInterface.OnClickListener clickListener,
                                String... text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text[0]).setMessage(text[1])
                .setPositiveButton(text[2], clickListener)
                .setNegativeButton(text[3], clickListener);
        AlertDialog dialog = builder.show();
        dialog.setCancelable(false);
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(15);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        lp.width = (int) (metric.widthPixels - 50 * metric.density);
        dialog.getWindow().setAttributes(lp);
    }


    public void showHierarchyFragment(Fragment fragment, int containerViewId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerViewId, fragment);
        ft.commitAllowingStateLoss();
    }
}
