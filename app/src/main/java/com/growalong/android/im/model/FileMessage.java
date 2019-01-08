package com.growalong.android.im.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.adapters.ChatAdapter;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.util.OpenFileUtil;
import com.growalong.android.util.Utils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMMessage;

import java.io.File;

/**
 * 文件消息
 */
public class FileMessage extends Message {


    public FileMessage(TIMMessage message) {
        this.message = message;
    }

    public FileMessage(String filePath) {
        message = new TIMMessage();
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(filePath);
        elem.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        message.addElement(elem);
    }

    @Override
    public String getContent() {
        return ((TIMFileElem) message.getElement(0)).getPath();
    }

    @Override
    public String[] getInfo() {
        return new String[0];
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) return;
        final TIMFileElem e = (TIMFileElem) message.getElement(0);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_im_file, null, false);

        TextView tv = view.findViewById(R.id.title);
        tv.setText(e.getFileName());
        TextView size = view.findViewById(R.id.size);
        size.setText(Utils.FormetFileSize(e.getFileSize()));

        getBubbleView(viewHolder).addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileUtil.openFile(context, new File(e.getPath()));
            }
        });
        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return MyApplication.getInstance().context.getString(R.string.summary_file);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {
        if (message == null) return;
        final TIMFileElem e = (TIMFileElem) message.getElement(0);
        String[] str = e.getFileName().split("/");
        String filename = str[str.length - 1];
        if (FileUtil.isFileExist(filename, Environment.DIRECTORY_DOWNLOADS)) {
            Toast.makeText(MyApplication.getInstance().context, MyApplication.getInstance().context.getString(R.string.save_exist), Toast.LENGTH_SHORT).show();
            return;
        }

        e.getToFile(FileUtil.getCacheFilePath(filename), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "getFile failed. code: " + i + " errmsg: " + s);
            }

            @Override
            public void onSuccess() {

            }
        });

    }
}
