package com.kingsoft.shiyou.loggy.db.dynamodb;

import com.kingsoft.shiyou.loggy.db.LoggyUploadInfoStore;
import com.kingsoft.shiyou.loggy.db.model.UploadInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import static com.kingsoft.shiyou.loggy.utils.FunctionUtils.pcall;

/**
 * @author taoshuang on 2020/4/23.
 */
@Log4j2
public class DynamoDbUploadInfoTable implements LoggyUploadInfoStore {

    private final LoggyDynamoDbConfig config;
    private final DynamoDbEnhancedAsyncClient client;

    public DynamoDbUploadInfoTable(LoggyDynamoDbConfig config, DynamoDbEnhancedAsyncClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        if (config.createTableOnStartup()) {
            final var createTableEnhancedRequest = createTableEnhancedRequest(config);
            createTable(startPromise, createTableEnhancedRequest);
        } else {
            startPromise.complete();
        }
    }

    private CreateTableEnhancedRequest createTableEnhancedRequest(LoggyDynamoDbConfig config) {
        return CreateTableEnhancedRequest.builder()
                .provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(config.tableReadThroughput())
                .writeCapacityUnits(config.tableWriteThroughput())
                .build()).build();
    }

    private void createTable(Promise<Void> promise, CreateTableEnhancedRequest request) {
        var tableName = config.loggyUploadInfoTableName();
        client.table(tableName, TableSchema.fromBean(UploadInfo.class))
                .createTable(request).whenComplete((v, err) -> pcall(() -> {
            if (err == null) {
                log.info("DynamoDb table created successfully: {}", tableName);
                promise.complete();
                return;
            }
            if (err.getCause() instanceof ResourceInUseException) {
                log.info("DynamoDb table already created: {}", tableName);
                promise.complete();
                return;
            }
            promise.fail(err);
        }));
    }

    @Override
    public void saveUploadInfo(UploadInfo uploadInfo, Handler<AsyncResult<Void>> resultHandler) {

    }
}
