package com.riot.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;

@Data
@Accessors(chain = true)
public class AddPlayerRequest {

    @Min(value = 0, message = "MMR must be positive number")
    private int mmr;
}
