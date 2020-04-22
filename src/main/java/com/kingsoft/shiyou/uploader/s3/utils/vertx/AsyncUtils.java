package com.kingsoft.shiyou.uploader.s3.utils.vertx;

import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author taoshuang on 2020/4/22.
 */
public final class AsyncUtils {

    public static void startSequentially(Promise<Void> result, Async... inputs) {
        startSequentially(result, Arrays.asList(inputs));
    }

    public static void startSequentially(Promise<Void> result, List<? extends Async> inputs) {
        compose(result, inputs, Async::start);
    }

    public static void stopSequentially(Promise<Void> result, Async... inputs) {
        stopSequentially(result, Arrays.asList(inputs));
    }

    public static void stopSequentially(Promise<Void> result, List<? extends Async> inputs) {
        compose(result, inputs, Async::stop);
    }

    private static void compose(Promise<Void> result,
                                List<? extends Async> inputs,
                                BiConsumer<Async, Promise<Void>> consumer) {
        final int len = inputs.size();
        if (len > 0) {
            final Promise<Void> seed = Promise.promise();
            Future<Void> last = seed.future();
            for (Async async : inputs) {
                last = last.compose(v -> Future.future(p -> consumer.accept(async, p)));
            }
            last.setHandler(result);
            seed.complete();
        } else {
            result.complete();
        }
    }
}
