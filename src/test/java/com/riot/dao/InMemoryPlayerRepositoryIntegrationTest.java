package com.riot.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InMemoryPlayerRepositoryIntegrationTest {

    @Autowired
    private InMemoryPlayerRepository repository;

    @Test
    public void testInitialTotalCount() {
        Assertions.assertEquals(25_000, repository.getTotalPlayerCount());
    }
}
