package com.growalong.android.upload;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.growalong.android.app.Constants;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NoDataParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.IUploadApis;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by gangqing on 2016/9/1.
 */

public class OSSManager {
    private OSS oss;
    private static OSSManager mInstance;

    private OSSManager() {

    }

    public static OSSManager getInstance() {
        if (mInstance == null) {
            mInstance = new OSSManager();
        }
        return mInstance;
    }

    public OSS getOss() {
        if (oss == null) {
            oss = tryGetOSSInfo();
        }
        return oss;
    }

    public OSS tryGetOSSInfo() {
        //        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。
        OSSPlainTextAKSKCredentialProvider provider = new OSSPlainTextAKSKCredentialProvider("LTAIPaAt4EtxU7nP", "voPct4UhTVFLS68hGUzcKrhPiTtCH8");
//        OSSClient tClient = new OSSClient(ctx, endpoint, provider);

        OSS oss = new OSSClient(MyApplication.getInstance().context, Constants.endpoint, provider);
        return oss;
    }

    public void update() {
        if (oss != null) {
            OosDataModel ossParams = getOssParams();
            oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(ossParams.getAccessKeyId(), ossParams.getAccessKeySecret(), ossParams.getSecurityToken()));
        }
    }

    public OosDataModel getOssParams() {
        try {
            BaseParams<NoDataParams> baseParams = new BaseParams<>(new NoDataParams());
            IUploadApis ip = BaseRetrofitClient.getInstance().create(IUploadApis.class);
            Call<BaseGenericModel<OosDataModel>> call = ip.getUploadParams(baseParams);
            Response<BaseGenericModel<OosDataModel>> response = call.execute();
            BaseGenericModel<OosDataModel> bean = response.body();
            return bean.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
