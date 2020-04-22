package com.kingsoft.shiyou.uploader.s3;

import io.vertx.core.Launcher;

/**
 * @author taoshuang on 2020/4/22.
 */
public class Loggy extends Launcher {

    public static void main(String[] args) {
        new Loggy().dispatch(args);
    }
}
