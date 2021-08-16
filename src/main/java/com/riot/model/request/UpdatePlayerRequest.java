package com.riot.model.request;

import com.riot.model.Tier;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;

@Data
@Accessors(chain = true)
public class UpdatePlayerRequest {

    private long playerID;

    @Min(value = 0, message = "MMR must be positive number")
    private Integer mmr;

    private Tier tier;
}
