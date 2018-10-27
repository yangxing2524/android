package agora.openvcall;

import android.app.Application;

import agora.openvcall.model.AgoraCurrentUserSettings;
import agora.openvcall.model.AgoraWorkerThread;

public class AgoraAGApplication extends Application {

    private AgoraWorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new AgoraWorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized AgoraWorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final AgoraCurrentUserSettings mVideoSettings = new AgoraCurrentUserSettings();
}
