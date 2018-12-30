package com.growalong.android.im.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.model.Conversation;
import com.growalong.android.im.model.FriendProfile;
import com.growalong.android.im.model.FriendshipInfo;
import com.growalong.android.im.model.NomalConversation;
import com.growalong.android.im.utils.TimeUtil;
import com.growalong.android.ui.ChatActivity;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.Utils;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ConversationAdapter(Context context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);
            view.setTag(viewHolder);
        }
        final Conversation data = getItem(position);
        viewHolder.tvName.setText(data.getName());


        if (data instanceof NomalConversation) {
            NomalConversation nomalConversation = (NomalConversation) data;
            if (nomalConversation.getType() == TIMConversationType.C2C) {
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(data.getIdentify());
                if (profile != null) {
                    String url = profile.getAvatarUrl();
                    if (url == null) {
                        viewHolder.avatar.setImageResource(data.getAvatar());
                    } else {
                        Glide.with(viewHolder.avatar.getContext()).load(url).asBitmap().into(viewHolder.avatar);
                    }
                } else {
                    LogUtil.e("profile is null");
                }
            } else {
                viewHolder.avatar.setImageResource(data.getAvatar());
            }
        } else {
            viewHolder.avatar.setImageResource(data.getAvatar());
        }
        String lastMessage = data.getLastMessageSummary();
        if (!TextUtils.isEmpty(lastMessage) && lastMessage.startsWith("&video_chat_")) {
            if (lastMessage.startsWith(ChatActivity.VIDEO_CHAT_REQUEST) || TextUtils.equals(lastMessage, ChatActivity.VIDEO_CHAT_REFUSE)) {
                lastMessage = MyApplication.getContext().getResources().getString(R.string.video_chat_conversation);
            } else if (TextUtils.equals(lastMessage, ChatActivity.VIDEO_CHAT_FAILED) ||
                    TextUtils.equals(lastMessage, ChatActivity.VIDEO_CHAT_OVER)) {
                lastMessage = MyApplication.getContext().getResources().getString(R.string.video_chat_over_conversation);
            }
        } else {
            lastMessage = Utils.getIMTextNormal(lastMessage);
        }
        viewHolder.lastMessage.setText(lastMessage);

        viewHolder.time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0) {
            viewHolder.unread.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10) {
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point1));
            } else {
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point2));
                if (unRead > 99) {
                    unReadStr = getContext().getResources().getString(R.string.time_more);
                }
            }
            viewHolder.unread.setText(unReadStr);
        }
        return view;
    }

    public class ViewHolder {
        public TextView tvName;
        public CircleImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;

    }
}
