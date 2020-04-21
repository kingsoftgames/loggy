package com.kingsoft.shiyou.uploader.s3.constant;

/**
 * @author taoshuang on 2020/4/21.
 */
public final class UploadResult {

    public static final String SUCCESS = "OK";

    public static final String INVALID_JSON = "Json parsing failed";

    public static final String UNSUPPORTED_MEDIA_TYPE = "Please use the application/json content type";

    public static final String INTERNAL_SERVER_ERROR = "Server internal error";
}
