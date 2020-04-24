package com.kingsoft.shiyou.loggy.notify.token;


import com.kingsoft.shiyou.loggy.utils.vertx.Async;

/**
 * @author taoshuang on 2020/3/30.
 */
public interface AccessTokenManager extends Async {

    String getAccessToken();
}
