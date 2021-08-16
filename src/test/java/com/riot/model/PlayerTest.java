package com.riot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PlayerTest {

    @ParameterizedTest
    @MethodSource("provideForComparePlayers")
    void comparePlayers(final Player p1, final Player p2, final boolean isP1GtThanP2) {
        Assertions.assertEquals(isP1GtThanP2, p1.compareTo(p2) > 0);
    }

    private static Stream<Arguments> provideForComparePlayers() {
        return Stream.of(
                Arguments.of(new Player().setId(1).setMmr(100).setTier(Tier.CHALLENGER),
                        new Player().setId(2).setMmr(200).setTier(Tier.MASTER),
                        true),
                Arguments.of(new Player().setId(1).setMmr(100).setTier(Tier.CHALLENGER),
                        new Player().setId(2).setMmr(100).setTier(Tier.MASTER),
                        true),
                Arguments.of(new Player().setId(1).setMmr(200).setTier(Tier.CHALLENGER),
                        new Player().setId(2).setMmr(100).setTier(Tier.CHALLENGER),
                        true),
                Arguments.of(new Player().setId(1).setMmr(200).setTier(Tier.CHALLENGER),
                        new Player().setId(2).setMmr(200).setTier(Tier.CHALLENGER),
                        false)
        );
    }
}
