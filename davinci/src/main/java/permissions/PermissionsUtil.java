package permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by Youga on 2017/8/28.
 */

public class PermissionsUtil {

    static final String TAG = "PermissionsUtil";
    private final PermissionsFragment mPermissionsFragment;

    public PermissionsUtil(@NonNull Activity activity) {
        mPermissionsFragment = getPermissionsFragment(activity);
    }

    private PermissionsFragment getPermissionsFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        PermissionsFragment permissionsFragment = (PermissionsFragment) fragmentManager.findFragmentByTag(TAG);
        if (permissionsFragment == null) {
            permissionsFragment = new PermissionsFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionsFragment;
    }

    public void request(PermissionCallback callback, String permission) {
        if (isGranted(permission)) {
            callback.call(true, permission);
        }

        if (isRevoked(permission)) {
            // Revoked by a policy, return a denied Permission object.
            mPermissionsFragment.log("Revoked by a policy, return a denied Permission object");
        }

        String[] permissions = new String[]{permission};
        mPermissionsFragment.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mPermissionsFragment.requestPermissions(permissions, callback);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() || mPermissionsFragment.isGranted(permission);
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() && mPermissionsFragment.isRevoked(permission);
    }

    public interface PermissionCallback {
        void call(boolean isGranted, String... permission);
    }
}
