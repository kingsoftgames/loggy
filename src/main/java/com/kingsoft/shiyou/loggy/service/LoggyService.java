package com.kingsoft.shiyou.loggy.service;

import com.kingsoft.shiyou.loggy.service.api.v1.Api;
import com.kingsoft.shiyou.loggy.utils.vertx.Async;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.apache.logging.log4j.util.Unbox.box;

/**
 * @author taoshuang on 2020/4/22.
 */
@Log4j2
@Singleton
public final class LoggyService implements Async {

    private final Router rootRouter;
    private final Router apiRouter;

    @Inject
    HttpServer httpServer;

    @Inject
    Api api;

    @Inject
    public LoggyService(Vertx vertx) {
        rootRouter = Router.router(vertx);
        apiRouter = Router.router(vertx);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        registerRoutes();
        startHttpServer(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        stopHttpServer(stopPromise);
    }

    private void registerRoutes() {
        registerDefaultRoute();
        registerApiRoutes();
    }

    private void registerDefaultRoute() {
        var route = rootRouter.route();
        route.handler(BodyHandler.create());
        route.handler(LoggerHandler.create(false, LoggerFormat.SHORT));
        route.failureHandler(ErrorHandler.create(true));
    }

    private void registerApiRoutes() {
        api.registerRoutes(apiRouter);
        rootRouter.mountSubRouter("/v1", apiRouter);
    }

    private void startHttpServer(Promise<Void> promise) {
        httpServer.requestHandler(rootRouter).listen(ar -> {
            if (ar.succeeded()) {
                log.info("Started listening at http://localhost:{}", box(httpServer.actualPort()));
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
    }

    private void stopHttpServer(Promise<Void> promise) {
        httpServer.close(promise);
    }
}
