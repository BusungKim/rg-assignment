package com.riot.model.response;

import com.riot.model.RankedPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TopPlayersResponse {
    private int count;
    private List<RankedPlayer> players;
}
