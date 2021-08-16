package com.riot.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Player implements Comparable<Player> {
    private long id;
    private int mmr;
    private Tier tier;

    @Override
    public int compareTo(final Player player) {
        if (this.tier != player.getTier()) {
            return this.tier.ordinal() < player.getTier().ordinal() ? 1 : -1;
        }
        if (this.mmr != player.getMmr()) {
            return this.mmr > player.getMmr() ? 1 : -1;
        }
        if (this.id != player.getId()) {
            return this.id > player.getId() ? 1 : -1;
        }
        return 0;
    }
}
