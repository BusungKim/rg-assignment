package com.riot.model.response;

import com.riot.model.RankedPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayerResponse {
    private RankedPlayer rankedPlayer;
}
