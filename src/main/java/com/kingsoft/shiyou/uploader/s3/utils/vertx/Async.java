package com.kingsoft.shiyou.uploader.s3.utils.vertx;

import io.vertx.core.Promise;

/**
 * @author taoshuang on 2020/4/22.
 */
public interface Async {

    default void start(Promise<Void> startPromise) {
        startPromise.complete();
    }

    default void stop(Promise<Void> stopPromise) {
        stopPromise.complete();
    }
}
