package com.growalong.android.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by yangxing on 2017/6/19.
 */

public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.growalong.android.app.MyApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
