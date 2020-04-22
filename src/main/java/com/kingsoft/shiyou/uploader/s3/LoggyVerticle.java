package com.kingsoft.shiyou.uploader.s3;

import com.kingsoft.shiyou.uploader.s3.dagger.DaggerLoggyComponent;
import com.kingsoft.shiyou.uploader.s3.dagger.VertxModule;
import com.kingsoft.shiyou.uploader.s3.service.LoggyService;
import com.kingsoft.shiyou.uploader.s3.utils.vertx.AsyncUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import javax.inject.Inject;

/**
 * @author taoshuang on 2020/4/22.
 */
public class LoggyVerticle extends AbstractVerticle {

    @Inject
    LoggyService loggyService;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        injectDependencies();
        AsyncUtils.startSequentially(startPromise, loggyService);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        AsyncUtils.stopSequentially(stopPromise, loggyService);
    }

    private void injectDependencies() {
        DaggerLoggyComponent.builder()
                .vertxModule(new VertxModule(context))
                .build()
                .inject(this);
    }
}
