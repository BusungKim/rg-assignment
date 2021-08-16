package com.riot.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayerCountResponse {
    private int totalCountOfPlayers;
}
