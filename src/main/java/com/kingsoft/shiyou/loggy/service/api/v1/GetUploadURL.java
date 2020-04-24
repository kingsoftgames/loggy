package com.kingsoft.shiyou.loggy.service.api.v1;

import com.kingsoft.shiyou.loggy.LoggyConfig;
import com.kingsoft.shiyou.loggy.constant.Compression;
import com.kingsoft.shiyou.loggy.constant.UploadResult;
import com.kingsoft.shiyou.loggy.db.LoggyUploadInfoStore;
import com.kingsoft.shiyou.loggy.db.model.UploadInfo;
import com.kingsoft.shiyou.loggy.model.UploadRequest;
import com.kingsoft.shiyou.loggy.model.UploadResponse;
import com.kingsoft.shiyou.loggy.model.UploadResponse.UploadLogs;
import com.kingsoft.shiyou.loggy.service.validator.UploadValidator;
import com.kingsoft.shiyou.loggy.utils.StringUtils;
import com.kingsoft.shiyou.loggy.utils.http.HttpUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author taoshuang on 2020/4/22.
 */
@Log4j2
@Singleton
public final class GetUploadURL implements Handler<RoutingContext> {

    @Inject
    LoggyConfig loggyConfig;

    @Getter
    @Inject
    UploadValidator uploadValidator;

    @Inject
    Vertx vertx;

    @Inject
    LoggyUploadInfoStore uploadInfoStore;

    @Inject
    S3Presigner s3Presigner;

    private static final SimpleDateFormat SDF_DAY = new SimpleDateFormat("dd");
    private static final SimpleDateFormat SDF_MONTH = new SimpleDateFormat("MM");

    @Inject
    public GetUploadURL() {
    }

    @Override
    public void handle(RoutingContext rc) {
        var request = HttpUtils.parseRequest(rc, UploadRequest.class);
        if (request != null) {
            log.debug("Received request for channel {}, deviceId {}, os {}, os version {}, appVersion {}, appId {}, network {}",
                    request.getChannel(), request.getDeviceId(), request.getOs(), request.getOsVersion(),
                    request.getAppVersion(), request.getAppId(), request.getNetwork());
            handleRequest(rc, request);
        } else {
            rc.fail(400);
        }
    }

    private void handleRequest(RoutingContext rc, UploadRequest request) {
        var uploadContext = generateUploadContext(rc, request);
        uploadLogs(uploadContext, Instant.now(), ar -> handleUploadLogs(uploadContext, ar));
        var uploadInfo = generateUploadInfo(uploadContext);
        uploadInfoStore.saveUploadInfo(uploadInfo, ar -> {
            if (ar.succeeded()) {
                log.debug("Succeeded to save upload info, hash id {}, ts {}, s3 uri {}",
                        uploadInfo.getHashId(), uploadInfo.getTimestamp(), uploadInfo.getS3Uri());
            } else {
                log.error("Failed to save upload info", ar.cause());
            }
        });
    }

    private void uploadLogs(UploadContext context, Instant from, Handler<AsyncResult<PresignedPutObjectRequest>> handler) {
        var optionalCompression = Compression.of(loggyConfig.logsCompression());
        if (optionalCompression.isEmpty()) {
            var message = String.format("Unsupported compression format: %s", loggyConfig.logsCompression());
            handler.handle(Future.failedFuture(message));
            return;
        }

        var compression = optionalCompression.get();
        var s3Key = getS3Key(context.sessionId(), from, compression.extension());
        context.s3Key(s3Key);
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(loggyConfig.s3Bucket())
                .key(s3Key)
                .acl(ObjectCannedACL.fromValue(loggyConfig.s3ObjectAcl()))
                .contentType(compression.contentType())
                .build();

        var putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(loggyConfig.s3PresignTtl()))
                .putObjectRequest(putObjectRequest)
                .build();

        vertx.executeBlocking(future -> {
            var presignedRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
            future.complete(presignedRequest);
        }, ar -> {
            if (ar.succeeded()) {
                var presignedRequest = (PresignedPutObjectRequest) ar.result();
                handler.handle(Future.succeededFuture(presignedRequest));
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    private void handleUploadLogs(UploadContext context, AsyncResult<PresignedPutObjectRequest> ar) {
        if (ar.succeeded()) {
            var presignedRequest = ar.result();

            var uploader = generateUploadLogs(presignedRequest);
            context.uploadLogs(uploader);
            log.debug("The presigned url of client logs: {}", presignedRequest.url());

            var response = new UploadResponse()
                    .setMessage(UploadResult.SUCCESS)
                    .setUploadLogs(uploader);
            context.rc().response()
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encode(response));
        } else {
            log.error("Failed to upload logs", ar.cause());
            context.rc().fail(500);
        }
    }

    private UploadLogs generateUploadLogs(PresignedPutObjectRequest presignedRequest) {
        // signedHeaders = [
        //      "host" : ["rog2.s3.cn-north-1.amazonaws.com.cn"],
        //      "x-amz-acl": ["public-read"],
        //      ......
        // ]
        var headers = presignedRequest.signedHeaders().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.join(",", e.getValue())
                ));
        return new UploadLogs()
                .setUrl(presignedRequest.url().toString())
                .setMethod(presignedRequest.httpRequest().method().name())
                .setHeaders(headers)
                .setCompression(loggyConfig.logsCompression())
                .setExpires((int) presignedRequest.expiration().getEpochSecond());
    }

    private String getS3Key(String session, Instant from, String fileExtension) {
        var ldt = LocalDateTime.ofInstant(from, ZoneOffset.UTC);

        return String.format("%s%s/%s/%s/%s%s", loggyConfig.s3Prefix(), ldt.getYear(),
                SDF_DAY.format(ldt.getMonth().getValue()), SDF_MONTH.format(ldt.getDayOfMonth()), session, fileExtension);
    }

    private UploadInfo generateUploadInfo(UploadContext context) {
        var message = context.message();
        var s3Uri = String.format("s3://%s/%s", loggyConfig.s3Bucket(), context.s3Key());
        return UploadInfo.builder()
                .hashId(context.sessionId())
                .timestamp(message.getBatchTimestamp())
                .appId(message.getAppId())
                .appVersion(message.getAppVersion())
                .appVersionCode(message.getAppVersionCode())
                .buildNumber(message.getBuildNumber())
                .channel(message.getChannel())
                .deviceBrand(message.getDeviceBrand())
                .deviceId(message.getDeviceId())
                .deviceModel(message.getDeviceModel())
                .deviceScreen(message.getDeviceScreen())
                .network(message.getNetwork())
                .os(message.getOs())
                .osVersion(message.getOsVersion())
                .sgVersion(message.getSgVersion())
                .packageName(message.getPackageName())
                .s3Uri(s3Uri)
                .build();
    }

    private UploadContext generateUploadContext(RoutingContext rc, UploadRequest message) {
        var uuid = message.getBatchDataId();
        var ts = message.getBatchTimestamp();
        var deviceId = message.getDeviceId();
        var unHashSession = uuid + deviceId + ts;

        return new UploadContext()
                .uuid(uuid)
                .ts(ts)
                .sessionId(StringUtils.md5(unHashSession))
                .rc(rc)
                .message(message);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    private static final class UploadContext {
        private String uuid;
        private String ts;
        /**
         * hash(device_id + uuid + ts)
         */
        private String sessionId;
        private RoutingContext rc;
        private UploadRequest message;
        private JsonObject logs;
        private UploadLogs uploadLogs;
        private String s3Key;
    }
}
