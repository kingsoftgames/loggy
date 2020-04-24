package com.kingsoft.shiyou.loggy.dagger;

import com.kingsoft.shiyou.loggy.DynamoDbConfig;
import com.kingsoft.shiyou.loggy.db.LoggyLogsStore;
import com.kingsoft.shiyou.loggy.db.dynamodb.DynamoDbLoggyLogsTable;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import javax.inject.Singleton;

/**
 * @author taoshuang on 2020/4/23.
 */
@Module
public class DbModule {

    @Provides
    @Singleton
    LoggyLogsStore provideLoggyLogsStore(DynamoDbConfig config, DynamoDbEnhancedAsyncClient client) {
        return new DynamoDbLoggyLogsTable(config, client);
    }
}
