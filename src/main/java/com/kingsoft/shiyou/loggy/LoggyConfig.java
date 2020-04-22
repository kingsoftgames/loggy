package com.kingsoft.shiyou.loggy;

import org.aeonbits.owner.Config;

/**
 * @author taoshuang on 2020/4/21.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:loggy.properties",
        "file:/local/loggy.properties",
})
public interface LoggyConfig extends Config {

    /**
     * Server listen host (HTTP)
     */
    @Key("http.host")
    @DefaultValue("0.0.0.0")
    String httpHost();

    /**
     * Server listen port (HTTP)
     */
    @Key("http.port")
    @DefaultValue("9999")
    int httpPort();


    @Key("s3.region")
    @DefaultValue("cn-north-1")
    String s3Region();

    /**
     * S3 bucket to upload log file
     */
    @Key("s3.bucket")
    @DefaultValue("")
    String s3Bucket();


    /**
     * S3 acl to access objects
     */
    @Key("s3.object.acl")
    @DefaultValue("private")
    String s3ObjectAcl();

    /**
     * S3 prefix
     */
    @Key("s3.prefix")
    @DefaultValue("")
    String s3Prefix();

    /**
     * Specifies the duration for which this presigned request should be valid.
     * After this time has expired, attempting to use the presigned request will fail. 
     */
    @Key("s3.presign.ttl")
    @DefaultValue("600")
    long s3PresignTtl();

    /**
     * Use S3 acceleration
     */
    @Key("s3.acceleration.enabled")
    @DefaultValue("false")
    boolean s3AccelerationEnabled();

    /**
     * S3 acceleration uri
     * See https://docs.aws.amazon.com/AmazonS3/latest/dev/transfer-acceleration.html
     */
    @Key("s3.acceleration.uri")
    @DefaultValue("")
    String s3AccelerationUri();

    /**
     * The logs compression format
     */
    @Key("logs.compression")
    @DefaultValue("GZIP")
    String logsCompression();
}
