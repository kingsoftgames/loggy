package com.kingsoft.shiyou.loggy;

import io.vertx.core.Launcher;

/**
 * @author taoshuang on 2020/4/22.
 */
public class Loggy extends Launcher {

    static {
        // Tell java.util.logging to use log4j2
        // https://logging.apache.org/log4j/2.0/log4j-jul/index.html
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

        // Tell Vert.x to use log4j2 as the logging framework
        // See: https://vertx.io/docs/vertx-core/java/#_logging
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    }

    public static void main(String[] args) {
        new Loggy().dispatch(args);
    }
}
