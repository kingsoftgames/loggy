package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.LoggyConfig;
import com.kingsoft.shiyou.loggy.notify.client.NoticeClient;
import com.kingsoft.shiyou.loggy.notify.client.impl.FeishuClient;
import com.kingsoft.shiyou.loggy.notify.token.feishu.FeishuAccessTokenManager;
import com.kingsoft.shiyou.loggy.notify.token.feishu.FeishuAppInfo;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/24.
 */
@Module
public class NoticeModule {

    @Provides
    @Singleton
    WebClient provideWebClient(Vertx vertx) {
        return WebClient.create(vertx);
    }

    @Provides
    @Singleton
    NoticeClient provideNoticeClient(LoggyConfig config,
                                     Vertx vertx,
                                     WebClient webClient,
                                     FeishuAccessTokenManager accessTokenManager) {
        return new FeishuClient(
                vertx,
                webClient,
                config.feishuChannel(),
                accessTokenManager,
                config.feishuRequestTimeoutMs());
    }

    @Provides
    @Singleton
    FeishuAccessTokenManager provideFeishuAccessTokenManager(Vertx vertx,
                                                             LoggyConfig config,
                                                             WebClient webClient,
                                                             FeishuAppInfo appInfo) {
        return new FeishuAccessTokenManager(
                vertx,
                webClient,
                appInfo,
                config.feishuRequestTimeoutMs()
        );
    }

    @Provides
    @Singleton
    FeishuAppInfo provideFeishuAppInfo(LoggyConfig config) {
        var appId = getenv("FEISHU_APP_ID", config.feishuAppId());
        var appSecret = getenv("FEISHU_APP_SECRET", config.feishuAppSecret());
        return new FeishuAppInfo(appId, appSecret);
    }

    private static String getenv(String envName, String def) {
        var env = System.getenv(envName);
        return env != null ? env : def;
    }
}
