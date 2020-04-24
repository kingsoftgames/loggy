package com.kingsoft.shiyou.loggy.db.dynamodb;

import com.kingsoft.shiyou.loggy.DynamoDbConfig;
import com.kingsoft.shiyou.loggy.db.LoggyLogsStore;
import com.kingsoft.shiyou.loggy.db.model.Logs;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.util.Objects;

import static com.kingsoft.shiyou.loggy.utils.FunctionUtils.pcall;

/**
 * @author taoshuang on 2020/4/23.
 */
@Log4j2
public class DynamoDbLoggyLogsTable implements LoggyLogsStore {

    private static final Expression PUT_INFO_CON_EX = Expression.builder()
            .expression("attribute_not_exists(hashId)").build();
    private static final TableSchema<Logs> TABLE_SCHEMA = TableSchema.fromBean(Logs.class);

    private final DynamoDbConfig config;
    private final DynamoDbEnhancedAsyncClient client;
    private final String tableName;

    public DynamoDbLoggyLogsTable(DynamoDbConfig config, DynamoDbEnhancedAsyncClient client) {
        this.config = config;
        this.client = client;
        this.tableName = config.loggyLogsTableName();
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

    private CreateTableEnhancedRequest createTableEnhancedRequest(DynamoDbConfig config) {
        return CreateTableEnhancedRequest.builder()
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(config.tableReadThroughput())
                        .writeCapacityUnits(config.tableWriteThroughput())
                        .build()).build();
    }

    private void createTable(Promise<Void> promise, CreateTableEnhancedRequest request) {
        client.table(tableName, TABLE_SCHEMA)
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
    public void saveLogs(Logs logs, Handler<AsyncResult<Void>> resultHandler) {
        Objects.requireNonNull(resultHandler, "resultHandler");

        var request = PutItemEnhancedRequest.builder(Logs.class)
                .item(logs)
                .conditionExpression(PUT_INFO_CON_EX)
                .build();

        client.table(tableName, TABLE_SCHEMA)
                .putItem(request).whenComplete((v, err) -> pcall(() -> {
            if (err == null) {
                resultHandler.handle(Future.succeededFuture());
                return;
            }
            log.error("Failed to put logs on DynamoDb", err);
            resultHandler.handle(Future.failedFuture(err));
        }));
    }
}
