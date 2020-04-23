package com.kingsoft.shiyou.loggy.db;

import com.kingsoft.shiyou.loggy.db.model.UploadInfo;
import com.kingsoft.shiyou.loggy.utils.vertx.Async;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author taoshuang on 2020/4/23.
 */
public interface LoggyUploadInfoStore extends Async {

    void saveUploadInfo(UploadInfo uploadInfo, Handler<AsyncResult<Void>> resultHandler);
}
