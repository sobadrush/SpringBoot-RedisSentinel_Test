package com.nanshan.springbootredissentinel_test;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * @author RogerLo
 * @date 2023/10/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BaseTest {

    @LocalServerPort
    protected int localServerPort;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("========= [beforeAll] =========");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("========= [afterAll] =========");
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        System.out.println("========= [Setup] =========");
        System.out.println("【" + testInfo.getDisplayName() + "】");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("========= [tearDown] =========");
    }

}
