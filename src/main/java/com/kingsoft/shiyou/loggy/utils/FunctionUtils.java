package com.kingsoft.shiyou.loggy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author taoshuang on 2020/4/23.
 */
public final class FunctionUtils {

    private static final Logger log = LogManager.getLogger(FunctionUtils.class);

    /**
     * Call the given Runnable in protected mode,
     * So that any unhandled exception does not propagate.
     * <p>
     * See: https://www.lua.org/manual/5.3/manual.html#pdf-pcall
     */
    public static void pcall(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.error("Unhandled exception", t);
            } else {
                log.error("Unhandled exception {}: {}", t.getClass().getName(), t.getMessage());
            }
        }
    }
}
