package com.kingsoft.shiyou.loggy.db.dynamodb;

import com.kingsoft.shiyou.loggy.DynamoDbConfig;

/**
 * @author taoshuang on 2020/4/23.
 */
public interface LoggyDynamoDbConfig extends DynamoDbConfig {

    @Key("table.name.loggy.upload.info")
    @DefaultValue("loggy.upload.info")
    String loggyUploadInfoTableName();
}
