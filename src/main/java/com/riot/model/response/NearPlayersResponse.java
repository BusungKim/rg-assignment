package com.riot.model.response;

import com.riot.model.RankedPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NearPlayersResponse {
    private int count;
    private int maxRank;
    private int minRank;
    private long pivotPlayerID;
    private List<RankedPlayer> nearPlayers;
}
