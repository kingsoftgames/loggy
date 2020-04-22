package com.kingsoft.shiyou.loggy.service.api.v1;

import com.kingsoft.shiyou.loggy.service.validator.UploadValidator;
import io.vertx.ext.web.Router;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/22.
 */
@Singleton
public final class Api {

    @Inject
    UploadValidator uploadValidator;

    @Inject
    GetUploadURL getUploadURL;

    @Inject
    public Api() {
    }

    public void registerRoutes(Router router) {
        router.post("/GetUploadURL").handler(uploadValidator).handler(getUploadURL);
    }
}
