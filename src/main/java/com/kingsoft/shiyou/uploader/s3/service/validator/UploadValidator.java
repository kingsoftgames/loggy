package com.kingsoft.shiyou.uploader.s3.service.validator;

import com.kingsoft.shiyou.uploader.s3.constant.UploadResult;
import com.kingsoft.shiyou.uploader.s3.model.UploadResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/22.
 */
@Log4j2
@Singleton
public final class UploadValidator implements Handler<RoutingContext> {

    private static final String CONTENT_TYPE = "application/json";

    @Inject
    public UploadValidator() {
    }

    @Override
    public void handle(RoutingContext rc) {
        if (validateContentType(rc)) {
            rc.next();
        } else {
            var response = new UploadResponse(UploadResult.UNSUPPORTED_MEDIA_TYPE);
            rc.response()
                    .setStatusCode(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE.code())
                    .end(response.toJson());
        }
    }

    private boolean validateContentType(RoutingContext rc) {
        var contentType = rc.request().getHeader("Content-Type");
        return CONTENT_TYPE.equals(contentType);
    }
}
