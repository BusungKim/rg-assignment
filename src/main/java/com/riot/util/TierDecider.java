package com.riot.util;

import com.riot.model.Tier;

import java.util.HashMap;
import java.util.Map;

public class TierDecider {

    private final int totalPlayerCount;
    private final Map<Tier, Integer> tierToMinRank = new HashMap<>();

    public TierDecider(final int totalPlayerCount) {
        this.totalPlayerCount = totalPlayerCount;

        for (Tier tier : Tier.values()) {
            if (tier == Tier.CHALLENGER) {
                tierToMinRank.put(Tier.CHALLENGER, 100);
                continue;
            }

            tierToMinRank.put(tier, (int)(totalPlayerCount * tier.getMinRankPercent()));
        }
    }

    public Tier getPlayerTier(final int rank) {
        for (Tier tier : Tier.values()) {
            if (rank <= tierToMinRank.get(tier)) {
                return tier;
            }
        }
        return Tier.BRONZE;
    }
}
