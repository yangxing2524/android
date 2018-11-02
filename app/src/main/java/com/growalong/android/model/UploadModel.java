package com.growalong.android.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.growalong.android.account.AccountManager;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by gangqing on 2016/12/19.
 */

public class UploadModel implements Parcelable {
    public static final String ID = "id";
    public static final String USER_ID = "userId";
    public static final String TOPIC_ID = "topicId";
    public static final String STATE = "state";
    public static final String LOCAL_FILE_PATH = "localFilePath";
    public static final String FILE_TYPE = "fileType";
    public static final String FILE_SIZE = "fileSize";
    public static final String SECOND = "second";
    public static final String TEXT_CONTENT = "content";
    public static final String FILE_ID = "fileId";
    public static final String LIVE_ID = "liveId";
    public static final String UNIQUE_ID = "uniqueId";
    public static final String OSS_FILE_PATH = "ossFilePath";
    public static final String CREATE_TIME = "create_time";
    public static final String OBJECT_KEY = "object_key";

    //文件类型
    public static final String TYPE_TEXT = "text";  //文字
    public static final String TYPE_MIC_TEXT = "mic-text";   //音频(amr)
    public static final String FILE_TYPE_IMAGE = "image";   //图片
    public static final String FILE_TYPE_AUDIO = "audio";   //音频(amr)
    public static final String FILE_TYPE_MIC_AUDIO = "mic-audio";   //音频(amr)

    public static final String FILE_TYPE_APP_AUDIO = "appAudio";    //音频（aac）
    public static final String FILE_TYPE_M4A_AUDIO = "m4aAudio";    //音频（m4a）
    public static final String FILE_TYPE_MP3_AUDIO = "mp3Audio";    //音频（mp3）
    public static final String TYEP_VIDEO = "video";    //视频

    //文件上传状态    1:等待上传    2：正在上传  3：上传阿里云失败   4：上传服务器失败
    public static final int UPLOAD_STATE_WAIT = 1;
    public static final int UPLOAD_STATE_UPLOADING = 2;
    public static final int UPLOAD_STATE_OSS_FAIL = 3;
    public static final int UPLOAD_STATE_SERVICE_FAIL = 4;

    private int id;  //保存时不用传，数据库自动生成的id
    private String userId;
    private String topicId;
    private int state;   //1:等待上传    2：正在上传  3：上传阿里云失败   4：上传服务器失败  上面已经定义
    private String localFilePath;    //本地文件路经
    private String fileType;    //图片："image"    语音："audio"  上面已经定义
    private long fileSize;  //文件大小：用于判断是否是原来的文件
    private int second;  //语音秒数
    private String content;
    private String fileId;  //绑定PPT对应的图片的id
    private String liveId;
    private String uniqueId;
    private String serverId;   //上传成功后，服务器返回的Id
    private String ossFilePath;   //阿里云文件路径
    private String objectKey;
    private long createTime;//创建时间

    public UploadModel() {
    }

    public UploadModel(String userId, String topicId, int state, String localFilePath, String fileType, long fileSize, int second, String fileId, String liveId, String uniqueId, String content, long createTime) {
        this.userId = userId;
        this.topicId = topicId;
        this.state = state;
        this.localFilePath = localFilePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.second = second;
        this.content = content;
        this.fileId = fileId;
        this.liveId = liveId;
        this.uniqueId = uniqueId;
        this.createTime = createTime;
    }

    protected UploadModel(Parcel in) {
        id = in.readInt();
        userId = in.readString();
        topicId = in.readString();
        state = in.readInt();
        localFilePath = in.readString();
        fileType = in.readString();
        fileSize = in.readLong();
        second = in.readInt();
        content = in.readString();
        fileId = in.readString();
        liveId = in.readString();
        uniqueId = in.readString();
        serverId = in.readString();
        ossFilePath = in.readString();
        createTime = in.readLong();
        objectKey = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userId);
        dest.writeString(topicId);
        dest.writeInt(state);
        dest.writeString(localFilePath);
        dest.writeString(fileType);
        dest.writeLong(fileSize);
        dest.writeInt(second);
        dest.writeString(content);
        dest.writeString(fileId);
        dest.writeString(liveId);
        dest.writeString(uniqueId);
        dest.writeString(serverId);
        dest.writeString(ossFilePath);
        dest.writeLong(createTime);
        dest.writeString(objectKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadModel> CREATOR = new Creator<UploadModel>() {
        @Override
        public UploadModel createFromParcel(Parcel in) {
            return new UploadModel(in);
        }

        @Override
        public UploadModel[] newArray(int size) {
            return new UploadModel[size];
        }
    };

    public String getOssFilePath() {
        return ossFilePath;
    }

    public void setOssFilePath(String ossFilePath) {
        this.ossFilePath = ossFilePath;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFileType() {
        return fileType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }


    public static UploadModel getUploadModel(String ossFilePath, String path, String liveId, String fileId, String topicId, int state, String type) {
        UploadModel uploadModel = new UploadModel();
        uploadModel.setOssFilePath(ossFilePath);
        uploadModel.setLocalFilePath(path);
        uploadModel.setUniqueId(getImageViewUnique());
        uploadModel.setLiveId(liveId);
        uploadModel.setFileId(fileId);
        uploadModel.setTopicId(topicId);
        uploadModel.setState(state);
        uploadModel.setFileType(type);
        uploadModel.setUserId(AccountManager.getInstance().getAccountInfo().getUserId());
        return uploadModel;
    }
    /**
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getImageViewUnique(){
        String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String r = String.valueOf((int)((Math.random()*9+1)*100000));
        String identity = name+r;
        return identity;
    }
}
