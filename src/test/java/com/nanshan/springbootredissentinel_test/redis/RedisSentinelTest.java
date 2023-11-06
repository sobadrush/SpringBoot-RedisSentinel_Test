package com.nanshan.springbootredissentinel_test.redis;

import com.nanshan.springbootredissentinel_test.BaseTest;
import io.lettuce.core.*;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class RedisSentinelTest extends BaseTest {

    @Test
    @DisplayName("[test_001]")
    // @Disabled
    public void test_001() {
        System.out.println("localServerPort : " + localServerPort);
    }

    @Test
    @DisplayName("[test_002] Standalone Master/Replica")
    @SneakyThrows
    // @Disabled
    public void test_002() {
        // 連接到已配置好 Master/Replica 的 Master 節點
        RedisURI uri = RedisURI.create("redis://sa123456@127.0.0.1:6379/0");
        RedisClient redisClient = RedisClient.create(uri);
        redisClient.setOptions(ClientOptions.builder()
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .timeoutOptions(TimeoutOptions.builder().fixedTimeout(Duration.ofSeconds(5)).build())
                .build());
        try(StatefulRedisMasterReplicaConnection<String, String> conn
                    = MasterReplica.connect(redisClient, StringCodec.UTF8, uri)) {
            System.out.println(".......Connected to Redis.......");
            conn.setReadFrom(ReadFrom.REPLICA_PREFERRED);

            RedisAsyncCommands<String, String> asyncCmd = conn.async();
            RedisFuture<String> future = asyncCmd.set("todo1", "{" +
                                                                "  \"userId\": 1," +
                                                                "  \"id\": 1," +
                                                                "  \"title\": \"Clean the Room\"," +
                                                                "  \"completed\": false" +
                                                                "}");
            future.thenAccept(result -> {
                System.out.println("Set operation result: " + result);
                // 檢查操作是否成功
                if ("OK".equals(result)) {
                    System.err.println("<<< 操作 Redis 成功!");
                } else {
                    System.err.println("<<< 操作 Redis 失敗!");
                }
            });
            Thread.sleep(2000); // 等待一些時間，讓非同步的 callback 觸發
        } finally {
            System.out.println("....... call shutdown .......");
            redisClient.shutdown();
        }
    }

    @Test
    @DisplayName("[test_003] 測試連線到 Redis Sentinel 本人")
    // @Disabled
    // ref. https://blog.csdn.net/FlyLikeButterfly/article/details/124496285
    public void test_003() {
        //sentinel
        RedisURI sentinelUri = RedisURI.builder()
            .withSentinel("127.0.0.1", 26379, "sa12345") // 哨兵地址和密码
            .withSentinel("127.0.0.1", 26380, "sa12345") // 哨兵地址和密码
            .withSentinel("127.0.0.1", 26381, "sa12345") // 哨兵地址和密码
            .withSentinelMasterId("mymaster") // 被監控的 Redis Master 群組名稱
            // .withPassword("sa123456".toCharArray()) // 設定被監控端的 Redis Master 密碼
            .build();
        RedisClient sentinelClient = RedisClient.create(sentinelUri);
        StatefulRedisSentinelConnection<String, String> sentinelConn = sentinelClient.connectSentinel();
        RedisSentinelCommands<String, String> sentinelCmd = sentinelConn.sync();
        System.out.println(sentinelCmd.info("sentinel"));
    }

    // FixMe: Cannot connect Redis Sentinel at redis://127.0.0.1:26381
    @Test
    @DisplayName("[test_004] 測試連線到 Redis Sentinel 使用 RedisURI.create")
    // @Disabled
    public void test_004() {

        // RedisURI uri = RedisURI.create("redis-sentinel://sa12345@127.0.0.1:26379/0#mymaster"); // 失敗
        // RedisURI uri = RedisURI.create("redis-sentinel://sa12345@127.0.0.1:26379,sa12345@127.0.0.1:26380,sa12345@127.0.0.1:26381/0#mymaster"); // 失敗

        RedisURI uri = RedisURI.builder()
            .withSentinel("127.0.0.1", 26379, "sa12345") // sentinel 本身的密碼
            .withSentinel("127.0.0.1", 26380, "sa12345") // sentinel 本身的密碼
            .withSentinel("127.0.0.1", 26381, "sa12345") // sentinel 本身的密碼
            .withSentinelMasterId("mymaster")
            .withPassword("sa123456") // 被監控端的密碼
            .build();

        // System.out.println(redisURI);
        RedisClient redisClient = RedisClient.create(uri);
        redisClient.setOptions(
            ClientOptions.builder()
                .autoReconnect(true)
                .timeoutOptions(TimeoutOptions.builder().fixedTimeout(Duration.ofSeconds(10)).build())
                .build()
        );
        StatefulRedisMasterReplicaConnection<String, String> connection
                = MasterReplica.connect(redisClient, StringCodec.UTF8, uri);
        connection.setReadFrom(ReadFrom.REPLICA_PREFERRED);
    }
}
