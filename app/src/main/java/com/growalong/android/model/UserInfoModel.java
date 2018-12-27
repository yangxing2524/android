package com.growalong.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.growalong.android.app.MyApplication;

/**
 * Created by yangxing on 2018/11/14.
 */
public class UserInfoModel implements Parcelable {
    private long id;
    private String mobile;      //手机号码
    private String headImgUrl;      //头像
    private String name;        //昵称
    private String cnName;        //
    private String enName;        //
    private String status;      //状态 1：正常，0禁用,2已配对
    private int age;            //年龄
    private String birthday;    //生日 YYYY-MM-DD
    private int gender;         //性别 1男，0女
    private String hobby;       //爱好
    private String address;     //家庭地址
    private String familyInfo;  //家庭介绍
    private int grade;          //用户级别 1,2,3
    private int nation;         //国籍身份 1,中国家庭 2，英国家庭
    private String type = MyApplication.TYPE;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected UserInfoModel(Parcel in) {
        id = in.readLong();
        mobile = in.readString();
        headImgUrl = in.readString();
        name = in.readString();
        cnName = in.readString();
        enName = in.readString();
        status = in.readString();
        age = in.readInt();
        birthday = in.readString();
        gender = in.readInt();
        hobby = in.readString();
        address = in.readString();
        familyInfo = in.readString();
        grade = in.readInt();
        nation = in.readInt();
        type = in.readString();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public static final Creator<UserInfoModel> CREATOR = new Creator<UserInfoModel>() {
        @Override
        public UserInfoModel createFromParcel(Parcel in) {
            return new UserInfoModel(in);
        }

        @Override
        public UserInfoModel[] newArray(int size) {
            return new UserInfoModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(String familyInfo) {
        this.familyInfo = familyInfo;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getNation() {
        return nation;
    }

    public void setNation(int nation) {
        this.nation = nation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mobile);
        dest.writeString(headImgUrl);
        dest.writeString(name);
        dest.writeString(cnName);
        dest.writeString(enName);
        dest.writeString(status);
        dest.writeInt(age);
        dest.writeString(birthday);
        dest.writeInt(gender);
        dest.writeString(hobby);
        dest.writeString(address);
        dest.writeString(familyInfo);
        dest.writeInt(grade);
        dest.writeInt(nation);
        dest.writeString(type);
    }
}
