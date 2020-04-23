package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.DynamoDbConfig;
import com.kingsoft.shiyou.loggy.utils.awssdk.VertxSdkClient;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Context;
import org.aeonbits.owner.ConfigFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author taoshuang on 2020/4/23.
 */
@Module
public class AwsModule {

    @Provides
    @Singleton
    DynamoDbConfig provideDynamoDbConfig() {
        return ConfigFactory.create(DynamoDbConfig.class);
    }

    @Provides
    @Singleton
    DynamoDbEnhancedAsyncClient provideDynamoDbEnhancedAsyncClient(Context context, DynamoDbConfig config) {
        var dynamoDbAsyncClient = provideDynamoDbAsyncClient(context, config);
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }

    private DynamoDbAsyncClient provideDynamoDbAsyncClient(Context context, DynamoDbConfig config) {
        var builder = newDynamoDbAsyncClientBuilder(config);
        return VertxSdkClient.withVertx(builder, context).build();
    }

    private DynamoDbAsyncClientBuilder newDynamoDbAsyncClientBuilder(DynamoDbConfig config) {
        var builder = DynamoDbAsyncClient.builder()
                .region(Region.of(config.region()));

        if (config.overrideEndpoint()) {
            try {
                builder.endpointOverride(new URI(config.endpointUrl()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return builder;
    }
}
