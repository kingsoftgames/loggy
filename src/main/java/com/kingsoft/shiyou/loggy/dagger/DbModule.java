package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.db.LoggyUploadInfoStore;
import com.kingsoft.shiyou.loggy.db.dynamodb.DynamoDbUploadInfoTable;
import com.kingsoft.shiyou.loggy.db.dynamodb.LoggyDynamoDbConfig;
import dagger.Module;
import dagger.Provides;
import org.aeonbits.owner.ConfigFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/23.
 */
@Module
public class DbModule {

    @Provides
    @Singleton
    LoggyDynamoDbConfig provideLoggyDynamoDbConfig() {
        return ConfigFactory.create(LoggyDynamoDbConfig.class);
    }

    @Provides
    @Singleton
    LoggyUploadInfoStore provideUploadInfoStore(LoggyDynamoDbConfig config, DynamoDbEnhancedAsyncClient client) {
        return new DynamoDbUploadInfoTable(config, client);
    }
}
