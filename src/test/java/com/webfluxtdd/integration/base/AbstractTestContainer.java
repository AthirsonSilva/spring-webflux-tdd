package com.webfluxtdd.integration.base;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class AbstractTestContainer {
    @Container
    private static final MongoDBContainer MONGODB_CONTAINER;

    static {
        MONGODB_CONTAINER = new MongoDBContainer("mongo:latest");
        MONGODB_CONTAINER.withExposedPorts(27017);
        MONGODB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB_CONTAINER::getReplicaSetUrl);
    }
}
