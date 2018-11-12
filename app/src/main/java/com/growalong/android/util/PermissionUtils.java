package com.growalong.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;

import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2017/6/23.
 */

public class PermissionUtils {
    public static final int DEFAULT_REQUEST_CODE = 10;
    private static final SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;
    private static final SimpleArrayMap<String, String> PERMISSION_HINT;
    private static List<String> mNeedsPermissionList = new ArrayList<>();

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(1);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
    }

    static {
        PERMISSION_HINT = new SimpleArrayMap<>(3);
        PERMISSION_HINT.put("android.permission.CAMERA", "摄像头");
        PERMISSION_HINT.put("android.permission.RECORD_AUDIO", "录音");
        PERMISSION_HINT.put("android.permission.READ_EXTERNAL_STORAGE", "读写本地文件");
    }

    public static boolean checkPermission(String[] permissionList, Activity activity) {
        if (activity == null) {
            activity = AppManager.getInstance().currentActivity();
        }
        return checkPermission(permissionList, activity, DEFAULT_REQUEST_CODE);
    }

    public static boolean checkPermission(String[] permissionList, Activity activity, int requestCode) {
        if (activity == null) {
            activity = AppManager.getInstance().currentActivity();
        }
        mNeedsPermissionList.clear();
        if (permissionList.length > 0) {
            for (String permission : permissionList) {
                checkSinglePermission(permission);
            }
        }
        if (mNeedsPermissionList.size() > 0) {
            applyPermission(activity, requestCode);
            return false;
        }
        return true;
    }

    public static boolean checkPermissionOnly(String[] permissionList, Activity activity) {
        if (activity == null) {
            activity = AppManager.getInstance().currentActivity();
        }
        mNeedsPermissionList.clear();
        if (permissionList.length > 0) {
            for (String permission : permissionList) {
                checkSinglePermission(permission);
            }
        }
        if (mNeedsPermissionList.size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前权限是否达到动态申请的要求
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
    }

    /**
     * 判断是否有当前权限
     * true:有 false:没有
     */
    private static boolean hasSelfPermission(String permission) {
        return ContextCompat.checkSelfPermission(MyApplication.getInstance().context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请结果处理
     *
     * @return true:所有申请的权限都已经被允许了；false：还有权限没有被申请
     */
    public static boolean requestPermissionsResult(Context context, String[] permissions, int[] grantResults) {
        List<String> noPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            int permissionCode = grantResults[i];
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                noPermissions.add(permissions[i]);
            }
        }
        if (noPermissions.size() == 0) {
            return true;
        } else {
            StringBuilder hint = new StringBuilder("你没有允许");
            for (int i = 0; i < noPermissions.size(); i++) {
                if (i != 0) {
                    hint.append("和");
                }
                hint.append(PERMISSION_HINT.get(noPermissions.get(i)));
            }
            hint.append("权限，请在【应用权限管理】打开");
            showRationaleDialog(context, hint.toString());
            return false;
        }
    }

    /**
     * @return true:已经有该权限，false没有该权限
     */
    private static void checkSinglePermission(String permission) {
        if (PermissionUtils.permissionExists(permission) && !PermissionUtils.hasSelfPermission(permission)) {
            mNeedsPermissionList.add(permission);
        }
    }

    /**
     * 申请权限
     */
    private static void applyPermission(Activity activity, int requestCode) {
        String[] permissions = new String[mNeedsPermissionList.size()];
        for (int i = 0; i < mNeedsPermissionList.size(); i++) {
            permissions[i] = mNeedsPermissionList.get(i);
        }
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    private static void showRationaleDialog(final Context context, String message) {
        new AlertDialog.Builder(context)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        //打开权限管理应用
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", "com.growalong.android", null));
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .setMessage(message)
                .show();
    }
}
