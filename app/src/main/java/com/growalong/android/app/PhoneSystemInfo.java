package com.growalong.android.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by gangqing on 2016/11/22.
 */

public class PhoneSystemInfo {
    public static String imei;
    public static String ch;

    public static void init(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        ch = WalleChannelReader.getChannel(context.getApplicationContext(), "unknow");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imei = mTm.getDeviceId();
    }


//    public static String getMacAddress(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Logger.e("mac:23");
//            String macAddress;
//            NetworkInterface networkInterface;
//            try {
//                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//                while (interfaces.hasMoreElements()) {
//                    NetworkInterface iF = interfaces.nextElement();
//
//                    byte[] addr = iF.getHardwareAddress();
//                    if (addr == null || addr.length == 0) {
//                        continue;
//                    }
//
//                    StringBuilder buf = new StringBuilder();
//                    for (byte b : addr) {
//                        buf.append(String.format("%02X:", b));
//                    }
//                    if (buf.length() > 0) {
//                        buf.deleteCharAt(buf.length() - 1);
//                    }
//                    String mac = buf.toString();
//                    Logger.e("interfaceName=" + iF.getName() + ", mac=" + mac);
//                }
//
//
//                networkInterface = NetworkInterface.getByName("wlan0");
//                if (networkInterface == null) {
//                    return "02:00:00:00:00:02";
//                }
//                byte[] addr = networkInterface.getHardwareAddress();
//                StringBuffer buf = new StringBuffer();
//                for (byte b : addr) {
//                    buf.append(String.format("%02X:", b));
//                }
//                if (buf.length() > 0) {
//                    buf.deleteCharAt(buf.length() - 1);
//                }
//                macAddress = buf.toString();
//            } catch (SocketException e) {
//                e.printStackTrace();
//                return "02:00:00:00:00:02";
//            }
//            return macAddress;
//        } else {
//            Logger.e("mac:");
//            //该方法在6.0 系统 不再开发，返回 02:00:00:00:00:00
//            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//            return wifiInfo.getMacAddress();
//        }
//    }
}
