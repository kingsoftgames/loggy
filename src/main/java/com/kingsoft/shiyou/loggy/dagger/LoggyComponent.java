package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.LoggyVerticle;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/22.
 */
@Singleton
@Component(modules = {
    LoggyModule.class,
    VertxModule.class,
    AwsModule.class,
    DbModule.class,
    NoticeModule.class
})
public interface LoggyComponent {
    void inject(LoggyVerticle verticle);
}
