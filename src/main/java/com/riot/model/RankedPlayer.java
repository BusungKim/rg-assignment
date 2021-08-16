package com.riot.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RankedPlayer {
    private Player player;
    private int rank;
}
