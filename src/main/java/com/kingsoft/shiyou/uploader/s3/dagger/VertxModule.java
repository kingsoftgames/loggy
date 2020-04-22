package com.kingsoft.shiyou.uploader.s3.dagger;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Context;
import io.vertx.core.Vertx;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/22.
 */
@Module
public class VertxModule {

    private final Context context;

    public VertxModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Vertx provideVertx() {
        return context.owner();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return this.context;
    }
}
