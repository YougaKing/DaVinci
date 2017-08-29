package permissions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Youga on 2017/8/28.
 */

public class PermissionsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private PermissionsUtil.PermissionCallback mCallback;
    private boolean mLogging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, PermissionsUtil.PermissionCallback callback) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        mCallback = callback;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults);
    }


    void onRequestPermissionsResult(String permissions[], int[] grantResults) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            mCallback.call(granted, permissions[i]);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    void log(String message) {
        if (mLogging) {
            Log.d(PermissionsUtil.TAG, message);
        }
    }
}
