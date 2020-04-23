package com.kingsoft.shiyou.loggy.db.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * @author taoshuang on 2020/4/21.
 */
@Getter
@Setter
@Accessors(chain = true)
@DynamoDbBean
public final class UploadInfo {
    String hashId;
    String timestamp;
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
    String s3Uri;

    @DynamoDbPartitionKey
    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    @DynamoDbSortKey
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
