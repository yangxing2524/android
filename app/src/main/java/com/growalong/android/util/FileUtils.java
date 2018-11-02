package com.growalong.android.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class FileUtils {

    private static String BASE_PATH;
    private static String STICKER_BASE_PATH;

    private static FileUtils mInstance;

    public static FileUtils getInst() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }

    public File getExtFile(String path) {
        return new File(BASE_PATH + path);
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @throws Exception
     */
    public static long getFolderSize(File file) {
        try {
            long size = 0;
            if (!file.exists()) {
                return size;
            } else if (!file.isDirectory()) {
                return file.length();
            }
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
            return size;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        try {
            new DecimalFormat("#.00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            String fileSizeString = "";
            String wrongSize = "0B";
            if (fileS == 0) {
                return wrongSize;
            }
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "MB";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "GB";
            }
            return fileSizeString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getBasePath(int packageId) {
        return STICKER_BASE_PATH + packageId + "/";
    }


    public void removeAddonFolder(int packageId) {
        String filename = getBasePath(packageId);
        File file = new File(filename);
        if (file.exists()) {
            delete(file);
        }
    }

    public void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public String getPhotoSavedPath() {
        return BASE_PATH + "stickercamera";
    }

    public String getPhotoTempPath() {
        return BASE_PATH + "stickercamera";
    }

    public String getSystemPhotoPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    }


    public static boolean createFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                mkdir(file.getParentFile());
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }

    public boolean writeSimpleString(File file, String string) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            fOut.write(string.getBytes());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.closeStream(fOut);
        }
    }


    //都是相对路径，一一对应
    public boolean copyAssetDirToFiles(Context context, String dirname) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] children = assetManager.list(dirname);
            for (String child : children) {
                child = dirname + '/' + child;
                String[] grandChildren = assetManager.list(child);
                if (0 == grandChildren.length)
                    copyAssetFileToFiles(context, child);
                else
                    copyAssetDirToFiles(context, child);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //都是相对路径，一一对应
    public boolean copyAssetFileToFiles(Context context, String filename) {
        return copyAssetFileToFiles(context, filename, getExtFile("/" + filename));
    }

    private boolean copyAssetFileToFiles(Context context, String filename, File of) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = context.getAssets().open(filename);
            createFile(of);
            os = new FileOutputStream(of);

            int readedBytes;
            byte[] buf = new byte[1024];
            while ((readedBytes = is.read(buf)) > 0) {
                os.write(buf, 0, readedBytes);
            }
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.closeStream(is);
            IOUtil.closeStream(os);
        }
    }

    public boolean renameDir(String oldDir, String newDir) {
        File of = new File(oldDir);
        File nf = new File(newDir);
        return of.exists() && !nf.exists() && of.renameTo(nf);
    }

    /**
     * 复制整个文件夹内容
     *
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) throws IOException {

        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return;
        }
        File newDirectory = new File(newPath);
        if (newDirectory.isDirectory() && !newDirectory.exists()) {
            newDirectory.mkdirs(); //如果文件夹不存在 则建立新文件夹
        }
        File a = new File(oldPath);
        String[] file = a.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file[i]);
            } else {
                temp = new File(oldPath + File.separator + file[i]);
            }

            String nf = newPath + "/" + (temp.getName()).toString();
            File newFile = new File(nf);
            if (temp.isFile() && !newFile.exists()) {
                createFile(newFile);
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(nf);
                byte[] b = new byte[4096];
                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {//如果是子文件夹
                copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }

    }

    /**
     * 复制单个文件
     */
    public void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                createFile(new File(newPath));
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小   
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inStream);
            IOUtil.closeStream(fs);
        }

    }

}
