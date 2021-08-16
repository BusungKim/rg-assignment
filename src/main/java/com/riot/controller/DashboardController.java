package com.riot.controller;

import com.riot.model.request.AddPlayerRequest;
import com.riot.model.request.UpdatePlayerRequest;
import com.riot.model.response.NearPlayersResponse;
import com.riot.model.response.PlayerCountResponse;
import com.riot.model.response.PlayerResponse;
import com.riot.model.response.TierResponse;
import com.riot.model.response.TopPlayersResponse;
import com.riot.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/dashboard/v1")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/players/total-count")
    public Callable<PlayerCountResponse> getTotalUserCount() {
        return dashboardService::getTotalUserCount;
    }

    @GetMapping("/player/{playerID}/tier")
    public Callable<TierResponse> getTierOfPlayer(@PathVariable final long playerID) {
        return () -> dashboardService.getTierOfPlayer(playerID);
    }

    @GetMapping("/players/top-10")
    public Callable<TopPlayersResponse> getTop10Players() {
        return () -> dashboardService.getTopPlayers(10);
    }

    @GetMapping("/player/{pivotPlayerID}/near/{range}")
    public Callable<NearPlayersResponse> findNearPlayers(@PathVariable final long pivotPlayerID,
                                                         @PathVariable final int range) {
        return () -> dashboardService.getNearPlayers(pivotPlayerID, range);
    }

    @PostMapping("/player/create")
    public Callable<PlayerResponse> addPlayer(@RequestBody @Validated final AddPlayerRequest addPlayerRequest) {
        return () -> dashboardService.addPlayer(addPlayerRequest);
    }

    @PostMapping("/player/update")
    public Callable<PlayerResponse> updatePlayer(@RequestBody @Validated final UpdatePlayerRequest updatePlayerRequest) {
        return () -> dashboardService.updatePlayer(updatePlayerRequest);
    }

    @DeleteMapping("/player/{playerID}")
    public Callable<Void> deletePlayer(@PathVariable final long playerID) {
        return () -> {
            dashboardService.deletePlayer(playerID);
            return null;
        };
    }
}
