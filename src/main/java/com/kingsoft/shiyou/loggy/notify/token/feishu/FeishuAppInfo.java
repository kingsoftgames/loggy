package com.kingsoft.shiyou.loggy.notify.token.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/3/30.
 */
@Getter
@Setter
@AllArgsConstructor
@Singleton
public final class FeishuAppInfo {
    @JsonProperty("app_id")
    String appId;
    @JsonProperty("app_secret")
    String appSecret;
}
