package com.riot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Tier {
    CHALLENGER(0),
    MASTER(0.01),
    DIAMOND(0.05),
    PLATINUM(0.1),
    GOLD(0.25),
    SILVER(0.65),
    BRONZE(1);

    private final double minRankPercent;
}
