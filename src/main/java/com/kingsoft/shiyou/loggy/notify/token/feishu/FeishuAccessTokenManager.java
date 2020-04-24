package com.kingsoft.shiyou.loggy.notify.token.feishu;

import com.kingsoft.shiyou.loggy.notify.token.AccessTokenManager;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/3/30.
 */
@Log4j2
@Singleton
public final class FeishuAccessTokenManager implements AccessTokenManager {

    private static final String TOKEN_ADDR = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/";
    private static final long RETRY_REFRESH_INTERVAL = 10L;

    private final Vertx vertx;
    private final String tokenAddr;
    private final FeishuAppInfo appInfo;
    private final long requestTimeoutMs;
    private final WebClient webClient;
    private String token;
    private long tokenRefreshInterval;
    private long timerId;

    @Inject
    public FeishuAccessTokenManager(Vertx vertx,
                                    WebClient webClient,
                                    FeishuAppInfo appInfo,
                                    long requestTimeout) {
        this.vertx = vertx;
        this.webClient = webClient;
        this.tokenAddr = TOKEN_ADDR;
        this.appInfo = appInfo;
        // Vertx cannot set timer < 1ms
        this.tokenRefreshInterval = 1L;
        this.requestTimeoutMs = requestTimeout;
        this.token = null;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        getAccessTokenFromApi(ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                refreshAccessToken();
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        vertx.cancelTimer(timerId);
        stopPromise.complete();
    }

    @Override
    public String getAccessToken() {
        return this.token;
    }

    private void refreshAccessToken() {
        timerId = vertx.setTimer(tokenRefreshInterval * 1000L, id -> getAccessTokenFromApi(ar -> {
            if (!ar.succeeded()) {
                this.tokenRefreshInterval = RETRY_REFRESH_INTERVAL;
            }
            refreshAccessToken();
        }));
    }

    private void getAccessTokenFromApi(Handler<AsyncResult<String>> resultHandler) {
        webClient.postAbs(tokenAddr)
            .timeout(requestTimeoutMs)
            .sendJson(appInfo, ar -> {
                if (ar.succeeded()) {
                    handleResponse(ar.result(), resultHandler);
                } else {
                    log.error("Failed to get access token from feishu", ar.cause());
                    resultHandler.handle(Future.failedFuture(ar.cause()));
                }
            });
    }

    private void handleResponse(HttpResponse<Buffer> response, Handler<AsyncResult<String>> resultHandler) {
        var statusCode = response.statusCode();
        var statusMsg = response.statusMessage();
        if (statusCode == 200) {
            var responseBody = response.bodyAsJsonObject();
            var code = responseBody.getInteger("code");
            if (code == 0) {
                this.token = responseBody.getString("tenant_access_token");
                this.tokenRefreshInterval = responseBody.getLong("expire");
                log.debug("Succeeded to get access token from feishu, expire in {}", tokenRefreshInterval);
                resultHandler.handle(Future.succeededFuture(this.token));
            } else {
                var errorMsg = responseBody.getString("msg");
                log.error("Failed to get access token from feishu, code: {}, message: {}", code, errorMsg);
                resultHandler.handle(Future.failedFuture(errorMsg));
            }
        } else {
            log.error("Failed to get access token from feishu, status: {}, message: {}", statusCode, statusMsg);
            resultHandler.handle(Future.failedFuture(statusMsg));
        }
    }
}
