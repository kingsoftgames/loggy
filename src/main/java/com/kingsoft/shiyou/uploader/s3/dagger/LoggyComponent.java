package com.kingsoft.shiyou.uploader.s3.dagger;

import com.kingsoft.shiyou.uploader.s3.LoggyVerticle;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/22.
 */
@Singleton
@Component(modules = {
    LoggyModule.class,
    VertxModule.class
})
public interface LoggyComponent {
    void inject(LoggyVerticle verticle);
}
