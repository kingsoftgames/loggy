package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.LoggyConfig;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import org.aeonbits.owner.ConfigFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import javax.inject.Singleton;
import java.net.URI;

/**
 * @author taoshuang on 2020/4/22.
 */
@Module
public class LoggyModule {

    @Provides
    @Singleton
    LoggyConfig provideLoggyConfig() {
        return ConfigFactory.create(LoggyConfig.class);
    }

    @Provides
    @Singleton
    HttpServer provideHttpServer(Vertx vertx, LoggyConfig config) {
        var options = new HttpServerOptions()
                .setHost(config.httpHost())
                .setPort(config.httpPort());
        return vertx.createHttpServer(options);
    }

    @Provides
    @Singleton
    S3Presigner provideS3Presigner(LoggyConfig config) {
        var builder = S3Presigner.builder();
        builder = config.s3AccelerationEnabled() ? builder.endpointOverride(URI.create(config.s3AccelerationEndpoint()))
                : builder.region(Region.of(config.s3Region()));
        return builder.build();
    }
}
