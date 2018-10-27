package agora.openvcall.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.growalong.android.R;

import java.util.ArrayList;

import agora.openvcall.model.AgoraMessage;

public class InChannelMessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AgoraMessage> mMsglist;

    protected final LayoutInflater mInflater;

    public InChannelMessageListAdapter(Activity activity, ArrayList<AgoraMessage> list) {
        mInflater = activity.getLayoutInflater();
        mMsglist = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.in_channel_message, parent, false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AgoraMessage msg = mMsglist.get(position);

        MessageHolder myHolder = (MessageHolder) holder;
        String sender = msg.getSender().name;
        if (TextUtils.isEmpty(sender)) {
            myHolder.itemView.setBackgroundResource(R.drawable.rounded_bg_blue);
        } else {
            myHolder.itemView.setBackgroundResource(R.drawable.rounded_bg);
        }
        myHolder.msgContent.setText(msg.getContent());
    }

    @Override
    public int getItemCount() {
        return mMsglist.size();
    }

    @Override
    public long getItemId(int position) {
        return mMsglist.get(position).hashCode();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        public TextView msgContent;

        public MessageHolder(View v) {
            super(v);
            msgContent = (TextView) v.findViewById(R.id.msg_content);
        }
    }
}
