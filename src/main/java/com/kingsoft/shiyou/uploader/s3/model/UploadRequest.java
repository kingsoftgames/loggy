package com.kingsoft.shiyou.uploader.s3.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author taoshuang on 2020/4/21.
 */
@Getter
@Setter
public final class UploadRequest {
    String batchDataId;
    String batchTimestamp;
    String channel;
    String deviceId;
    String os;
    String osVersion;
    String deviceBrand;
    String deviceModel;
    String packageName;
    String appVersion;
    String appVersionCode;
    String appId;
    String buildNumber;
    String sgVersion;
    String deviceScreen;
    String network;
}
