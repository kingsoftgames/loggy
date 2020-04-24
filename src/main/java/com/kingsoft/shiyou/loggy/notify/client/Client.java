package com.kingsoft.shiyou.loggy.notify.client;

import com.kingsoft.shiyou.loggy.model.UploadRequest;
import com.kingsoft.shiyou.loggy.utils.vertx.Async;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author taoshuang on 2020/4/24.
 */
public interface Client extends Async {

    void notify(UploadRequest request, String downloadUrl, Handler<AsyncResult<Void>> handler);
}
