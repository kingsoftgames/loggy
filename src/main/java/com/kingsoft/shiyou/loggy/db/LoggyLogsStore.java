package com.kingsoft.shiyou.loggy.db;

import com.kingsoft.shiyou.loggy.db.model.Logs;
import com.kingsoft.shiyou.loggy.utils.vertx.Async;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author taoshuang on 2020/4/23.
 */
public interface LoggyLogsStore extends Async {

    void saveLogs(Logs logs, Handler<AsyncResult<Void>> resultHandler);
}
