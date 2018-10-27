package agora.openvcall.model;

public class AgoraMessage {
    private AgoraUser mSender;

    private String mContent;

    private int mType;

    public AgoraMessage(int type, AgoraUser sender, String content) {
        mType = type;
        mSender = sender;
        mContent = content;
    }

    public AgoraMessage(AgoraUser sender, String content) {
        this(0, sender, content);
    }

    public AgoraUser getSender() {
        return mSender;
    }

    public String getContent() {
        return mContent;
    }

    public int getType() {
        return mType;
    }

    public static final int MSG_TYPE_TEXT = 1; // CHANNEL
}
