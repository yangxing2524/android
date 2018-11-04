package com.growalong.android.rxevent;

/**
 * 通知key
 * Created by gongkai on 16/6/27.
 */
public class NotificationEvent {
    /**
     * 直播间信息修改
     */
    public static final String LIVE_INFO_REFRESH = "live_info_refresh"; //未处理

    /**
     * 话题结束成功
     */
    public static final String TOPIC_END = "topic_end"; //未处理

    //账号信息改变
    public static final String ACCOUNT_INFO_CHANGE = "account_info_change";
    //登录成功
    public static final String LOGIN_SUCCESS = "login_success";
    //登录失败
    public static final String LOGIN_FAIL = "login_fail";
    //退出成功
    public static final String LOGOUT_SUCCESS = "logout_success";
    //话题信息改变
    public static final String TOPIC_INFO_CHANGE = "topic_info_change";
    //切换直播间
    public static final String SWITCHOVER_LIVE = "switchover_live";
    //创建直播间成功
    public static final String CREATE_LIVE_ROOM_SUCCESS = "create_live_room_success";
    //跳转到我的直播
    public static final String MY_LIVE = "my_live";
    //跳转到直播中心
    public static final String FIND_LIVE = "find_live";
    //跳转到推荐
    public static final String FIND_DYNAMIC = "find_dynamic";
    //跳转到动态
    public static final String DYNAMIC_EVENT = "dynamic_event";
    //直播中心－>选择分类完成
    public static final String SUBSCRIPTION_EVENT = "subscription_event";
    //创建话题成功
    public static final String CREATE_TOPIC_SUCCESS = "create_topic_success";
    //更新头衔
    public static final String UPDATE_ROLE = "update_role";
    //微信支付成功
    public static final String COMMAND_PAY_BY_WX_SUCCESS = "command_pay_by_wx";
    //微信支付－>取消支付
    public static final String CANCEL_PAY = "cancel_pay";
    //微信支付失败
    public static final String PAY_FAIL = "pay_fail";
    //零元支付成功
    public static final String PAY_FREE_SUCCESS = "pay_free_success";

    //本地录音失败
    public static final String RECORD_FAIL = "record_fail";

    //停止录音播放
    public static final String STOP_AUDIO_PLAY = "stop_audio_play";

    //话题移到频道成功
    public static final String MOVE_TO_CHANNEL = "move_to_channel";

    //系列课移动分类成功
    public static final String CHANNEL_MOVE = "channel_move";
    public static final String DeleteChannel = "delete_channel";
    //系列课删除成功
    public static final String CHANNEL_DELETE_SUCCESS = "channel_delete_success";
    //话题成员信息更改，包括删除、修改头衔
    public static final String TOPIC_MEMBER_UPDATE_INFO = "topic_member_update_info";

    //切换视频互动
    public static final String CHECK_VIDEO_INTERACT = "check_video_interact";
    //切换视频极简
    public static final String CHECK_VIDEO_MINIMAL = "check_video_minimal";

    //切换视频极简
    public static final String REFRESH_CURRENT_MODEL = "refresh_current_model";

}
