package com.kingsoft.shiyou.uploader.s3.constant;

import java.util.Optional;

/**
 * @author taoshuang on 2020/4/22.
 */
public enum Compression {

    NONE("NONE", ".log", "text/plain"),
    ZLIB("ZLIB", ".zlib", "application/zlib"),
    ZIP("ZIP", ".zip", "application/zip"),
    GZIP("GZIP", ".gz", "application/gzip");

    private final String compression;

    private final String extension;

    private final String contentType;

    public String compression() {
        return compression;
    }

    public String extension() {
        return extension;
    }

    public String contentType() {
        return contentType;
    }

    Compression(String compression, String extension, String contentType) {
        this.compression = compression;
        this.extension = extension;
        this.contentType = contentType;
    }

    public static Optional<Compression> of(String compression) {
        for (var c : Compression.values()) {
            if (c.compression().equals(compression)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
