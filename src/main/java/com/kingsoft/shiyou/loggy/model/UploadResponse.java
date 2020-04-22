package com.kingsoft.shiyou.loggy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author taoshuang on 2020/4/21.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public final class UploadResponse {
    /**
     * {@link com.kingsoft.shiyou.loggy.constant.UploadResult}
     */
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UploadLogs uploadLogs;

    public UploadResponse(String message) {
        this.message = message;
    }

    public String toJson() {
        return Json.encode(this);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class UploadLogs {
        private String url;
        private String method;
        private Map<String, String> headers;
        private String compression;
        private int expires;
    }
}
