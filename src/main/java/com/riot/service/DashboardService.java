package com.riot.service;

import com.riot.model.request.AddPlayerRequest;
import com.riot.model.request.UpdatePlayerRequest;
import com.riot.model.response.NearPlayersResponse;
import com.riot.model.response.PlayerCountResponse;
import com.riot.model.response.PlayerResponse;
import com.riot.model.response.TierResponse;
import com.riot.model.response.TopPlayersResponse;

public interface DashboardService {
    PlayerCountResponse getTotalUserCount();
    TierResponse getTierOfPlayer(long playerID);
    TopPlayersResponse getTopPlayers(int count);
    NearPlayersResponse getNearPlayers(long pivotPlayerID, int range);
    PlayerResponse addPlayer(AddPlayerRequest addPlayerRequest);
    PlayerResponse updatePlayer(UpdatePlayerRequest updatePlayerRequest);
    void deletePlayer(long playerID);
}
