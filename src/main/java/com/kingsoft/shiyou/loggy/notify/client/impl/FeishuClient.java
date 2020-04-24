package com.kingsoft.shiyou.loggy.notify.client.impl;

import com.kingsoft.shiyou.loggy.model.UploadRequest;
import com.kingsoft.shiyou.loggy.notify.client.Client;
import com.kingsoft.shiyou.loggy.notify.token.feishu.FeishuAccessTokenManager;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

/**
 * @author taoshuang on 2020/3/31.
 */
@Log4j2
@Singleton
public final class FeishuClient implements Client {

    private static final String PUSH_ADDR = "https://open.feishu.cn/open-apis/message/v4/send/";
    private static final long RETRY_PUSH_INTERVAL = 10L;
    private static final int MAX_RETRY = 3;
    private static final boolean IS_WIDE_SCREEN = true;

    private final Vertx vertx;
    private final WebClient webClient;
    private final String feishuPushAddr;
    private final String feishuChannel;
    private final FeishuAccessTokenManager accessTokenManager;
    private final long requestTimeoutMs;

    @Inject
    public FeishuClient(Vertx vertx,
                        WebClient webClient,
                        String feishuChannel,
                        FeishuAccessTokenManager accessTokenManager,
                        long requestTimeout) {
        this.vertx = vertx;
        this.webClient = webClient;
        this.feishuPushAddr = PUSH_ADDR;
        this.feishuChannel = feishuChannel;
        this.accessTokenManager = accessTokenManager;
        this.requestTimeoutMs = requestTimeout;
    }

    @AllArgsConstructor
    static class Retry {
        private int maxRetry;

        public void retry() {
            maxRetry--;
        }

        public boolean retryMax() {
            return maxRetry < 0;
        }
    }

    @Override
    public void start(Promise<Void> startPromise) {
        accessTokenManager.start(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        accessTokenManager.stop(stopPromise);
    }

    @Override
    public void notify(UploadRequest request, String downloadUrl, Handler<AsyncResult<Void>> handler) {
        log.info("Sending message to Feishu");
        var httpRequest = getHttpRequest();
        var jsonBody = getJsonBody(request, downloadUrl);
        sendRequest(httpRequest, jsonBody, "", new Retry(MAX_RETRY), ar -> {
            if (ar.succeeded()) {
                handler.handle(succeededFuture());
            } else {
                handler.handle(failedFuture(ar.cause()));
            }
        });
    }

    private void sendRequest(HttpRequest<Buffer> httpRequest,
                             JsonObject object,
                             String failureMessage,
                             Retry retry,
                             Handler<AsyncResult<Void>> handler) {
        if (retry.retryMax()) {
            log.error("Failed to push message to feishu, retry reached max value");
            handler.handle(failedFuture(failureMessage));
            return;
        }
        httpRequest.sendJsonObject(object, ar -> {
            if (ar.succeeded()) {
                var response = ar.result();
                if (response.statusCode() == 200) {
                    var responseBody = response.bodyAsJsonObject();
                    var code = responseBody.getInteger("code");
                    if (code == 0) {
                        log.info("Message sent to Feishu successfully");
                        handler.handle(succeededFuture());
                    } else {
                        retry.retry();
                        vertx.setTimer(RETRY_PUSH_INTERVAL * 1000L, id ->
                            sendRequest(httpRequest, object, responseBody.getString("msg"), retry, handler));
                    }
                } else {
                    retry.retry();
                    vertx.setTimer(RETRY_PUSH_INTERVAL * 1000L, id ->
                        sendRequest(httpRequest, object, response.bodyAsString(), retry, handler));
                }
            } else {
                retry.retry();
                vertx.setTimer(RETRY_PUSH_INTERVAL * 1000L, id ->
                    sendRequest(httpRequest, object, ar.cause().getMessage(), retry, handler));
            }
        });
    }

    private HttpRequest<Buffer> getHttpRequest() {
        var accessToken = accessTokenManager.getAccessToken();
        return webClient.postAbs(feishuPushAddr)
            .putHeader("Authorization", "Bearer " + accessToken)
            .timeout(requestTimeoutMs);
    }

    // See https://open.feishu.cn/document/ukTMukTMukTM/ukTNwUjL5UDM14SO1ATN
    private JsonObject getJsonBody(UploadRequest request, String downloadUrl) {
        var content = getContent(request, downloadUrl);
        return new JsonObject()
            .put("chat_id", feishuChannel)
            .put("msg_type", "interactive")
            .put("card", new JsonObject()
                .put("config", getConfig())
                .put("header", getTitle("Logs Reported"))
                .put("elements", getElements(content)));
    }

    private JsonObject getConfig() {
        return new JsonObject()
            .put("wide_screen_mode", IS_WIDE_SCREEN);
    }

    private static JsonObject getTitle(String title) {
        return new JsonObject()
            .put("title", getText("plain_text", title));
    }

    private static JsonArray getElements(String content) {
        List<JsonObject> elements = new ArrayList<>();
        elements.add(new JsonObject()
            .put("tag", "div")
            .put("text", getText("lark_md", content)));

        return new JsonArray(elements);
    }

    private static JsonObject getText(String tag, String content) {
        return new JsonObject()
            .put("tag", tag)
            .put("content", content);
    }

    private static String getContent(UploadRequest request, String downloadUrl) {
        var sb = new StringBuilder(512)
            .append("channel: **").append(request.getChannel()).append("**\n")
            .append("deviceBrand: **").append(request.getDeviceBrand()).append("**\n")
            .append("deviceModel: **").append(request.getDeviceModel()).append("**\n")
            .append("os: **").append(request.getOs()).append("**\n")
            .append("osVersion: **").append(request.getOsVersion()).append("**\n")
            .append("network: **").append(request.getDeviceScreen()).append("**\n")
            .append('\n')
            .append("appVersion: **").append(request.getAppVersion()).append("**\n")
            .append("appVersionCode: **").append(request.getAppVersionCode()).append("**\n")
            .append("appId: **").append(request.getAppId()).append("**\n")
            .append("buildNumber: **").append(request.getBuildNumber()).append("**\n")
            .append("deviceId: **").append(request.getDeviceId()).append("**\n")
            .append("deviceScreen: **").append(request.getDeviceScreen()).append("**\n")
            .append("sgVersion: **").append(request.getSgVersion()).append("**\n");

        appendLogsUrl(sb, downloadUrl);

        return sb.toString();
    }

    private static void appendLogsUrl(StringBuilder sb, String url) {
        assert sb != null;
        if (url != null) {
            sb.append("[Client Logs](").append(url).append(')').append('\n');
        }
    }
}
