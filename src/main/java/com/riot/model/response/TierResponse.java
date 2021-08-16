package com.riot.model.response;

import com.riot.model.Tier;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TierResponse {
    private Tier tier;
}
