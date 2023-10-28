package com.nanshan.springbootredissentinel_test.redis;

import com.nanshan.springbootredissentinel_test.BaseTest;
import io.lettuce.core.*;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;
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
    @DisplayName("[test_002] 測試連線到 Redis Sentinel 本人")
    // @Disabled
    // ref. https://blog.csdn.net/FlyLikeButterfly/article/details/124496285
    public void test_002() {
        //sentinel
        RedisURI sentinelUri = RedisURI.builder()
            .withSentinel("127.0.0.1", 26379, "sa12345") // 哨兵地址和密码
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
    @DisplayName("[test_003] 測試連線到 Redis Sentinel 使用 RedisURI.create")
    // @Disabled
    public void test_003() {
        RedisURI uri = RedisURI.create("redis-sentinel://sa12345@127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381/0#mymaster");
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
