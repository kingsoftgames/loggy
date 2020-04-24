package com.kingsoft.shiyou.loggy;

import org.aeonbits.owner.Config;

/**
 * @author taoshuang on 2020/4/23.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:dynamodb.properties",
        "file:/local/dynamodb.properties",
})
public interface DynamoDbConfig extends Config {

    @Key("region")
    @DefaultValue("cn-north-1")
    String region();

    @Key("overrideEndpoint")
    @DefaultValue("true")
    boolean overrideEndpoint();

    @Key("endpointUrl")
    @DefaultValue("http://localhost:8000")
    String endpointUrl();

    @Key("table.createOnStartup")
    @DefaultValue("true")
    boolean createTableOnStartup();

    @Key("table.onDemandBilling")
    @DefaultValue("true")
    boolean onDemandBilling();

    @Key("table.throughput.read")
    @DefaultValue("1")
    long tableReadThroughput();

    @Key("table.throughput.write")
    @DefaultValue("1")
    long tableWriteThroughput();

    @Key("table.name.loggy.logs")
    @DefaultValue("loggy.logs")
    String loggyLogsTableName();
}
