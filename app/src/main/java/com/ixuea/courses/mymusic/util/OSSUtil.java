package com.ixuea.courses.mymusic.util;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

/**
 * Created by smile on 2018/6/16.
 */

public class OSSUtil {

    private static OSSClient instance;

    public static OSSClient getInstance(Context context) {
        if (instance == null) {
            init(context);
        }
        return instance;
    }

    private static void init(Context context) {
        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新
        //我们这里为了课程难度，就直接使用AK和SK
        //OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);

        OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAIvvrRFHYlwoEj", "xDpTSeBM0PqDUWtlVPQfTfMb97CbPD");
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        instance = new OSSClient(context.getApplicationContext(), String.format(Consts.RESOURCE_PREFIX,""), credentialProvider);
    }
}
