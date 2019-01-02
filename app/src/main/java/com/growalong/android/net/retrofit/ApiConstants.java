package com.growalong.android.net.retrofit;

/**
 * Created by yangxing on 2018/10/31.
 */
public class ApiConstants {
    public static final String imgUpload = "v1/common/sts-auth";    //获取上传图片令牌

    public static final String loginForPhoneNumber = "/v1/common/login.do";
    public static final String loginForIM = "/v1/user/getUserSig.do"; //获取直播userSig
    public static final String courseList = "/v1/user/getMyCourse.do"; //用户课程列表
    public static final String courseDetailList = "/v1/course/get.do"; //课程基础信息
    public static final String courseMaterialList = "/v1/course/material.do"; //课程素材
    public static final String getUserInfo = "/v1/user/get.do"; //获取用户信息
    public static final String updateUserInfo = "/v1/user/update.do"; //更新用户信息
    public static final String getMyCollect = "/v1/user/getCollection.do"; //获取我的收藏
    public static final String addCollect = "/v1/user/collect.do"; //添加收藏
    public static final String removeCollect = "/v1/user/cancelCollect.do"; //取消收藏
    public static final String getRoom = "/v1/user/getRoom.do"; //获取房间号
    public static final String logout = "/v1/common/logout.do"; //退出登录
    public static final String getUserList = "/v1/user/getUserList.do"; //批量获取用户信息

}
