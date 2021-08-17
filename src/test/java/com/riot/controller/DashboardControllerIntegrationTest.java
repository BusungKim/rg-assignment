package com.riot.controller;

import com.riot.model.Player;
import com.riot.model.RankedPlayer;
import com.riot.model.Tier;
import com.riot.model.request.UpdatePlayerRequest;
import com.riot.model.response.NearPlayersResponse;
import com.riot.model.response.PlayerCountResponse;
import com.riot.model.response.PlayerResponse;
import com.riot.model.response.TierResponse;
import com.riot.model.response.TopPlayersResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DashboardControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testAPIs() {
        getTotalPlayerCountAndVerify(25_000);

        getTierAndVerify(721, Tier.CHALLENGER);

        getTop10PlayersAndVerify(Arrays.asList(13309L, 12360L, 5395L, 5450L, 15440L, 14684L, 14049L, 721L, 18346L, 18390L));

        getNearPlayersAndVerify(23463L, Arrays.asList(21014L, 15853L, 12163L, 23463L, 21138L, 20103L, 15075L));

        addPlayerAndVerify(4000, 1, Tier.CHALLENGER);

        getTotalPlayerCountAndVerify(25_001);

        updatePlayerAndVerify(new UpdatePlayerRequest().setPlayerID(13309).setTier(Tier.MASTER), 101, 3499, Tier.MASTER);

        getTierAndVerify(13309, Tier.MASTER);

        deletePlayer(13309);

        getTotalPlayerCountAndVerify(25_000);
    }

    void getTotalPlayerCountAndVerify(final int expectedTotalPlayerCount) {
        // given
        final String url = getEndpointURL("players/total-count");

        // when
        final ResponseEntity<PlayerCountResponse> responseEntity = restTemplate.getForEntity(url, PlayerCountResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTotalPlayerCount, responseEntity.getBody().getTotalCountOfPlayers());
    }

    void getTierAndVerify(final long playerID, final Tier expectedTier) {
        // given
        final String url = getEndpointURL(String.format("player/%d/tier", playerID));

        // when
        final ResponseEntity<TierResponse> responseEntity = restTemplate.getForEntity(url, TierResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTier, responseEntity.getBody().getTier());
    }

    void getTop10PlayersAndVerify(final List<Long> expectedTop10PlayerIDs) {
        // given
        final String url = getEndpointURL("players/top-10");

        // when
        final ResponseEntity<TopPlayersResponse> responseEntity = restTemplate.getForEntity(url, TopPlayersResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final List<RankedPlayer> players = responseEntity.getBody().getPlayers();
        assertTrue(players.stream().allMatch(p -> Tier.CHALLENGER == p.getPlayer().getTier()));

        assertEquals(expectedTop10PlayerIDs, players.stream().map(RankedPlayer::getPlayer).map(Player::getId).collect(Collectors.toList()));
    }

    void getNearPlayersAndVerify(final long pivotPlayerID, final List<Long> expectedPlayerIDs) {
        // given
        final String url = getEndpointURL(String.format("player/%d/near/3", pivotPlayerID));

        // when
        final ResponseEntity<NearPlayersResponse> responseEntity = restTemplate.getForEntity(url, NearPlayersResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final List<RankedPlayer> players = responseEntity.getBody().getNearPlayers();
        assertEquals(expectedPlayerIDs, players.stream().map(RankedPlayer::getPlayer).map(Player::getId).collect(Collectors.toList()));
    }

    void addPlayerAndVerify(final int mmr, final int expectedRank, final Tier expectedTier) {
        // given
        final String url = getEndpointURL("player/create");

        // when
        final Map<String, Object> requestBody = Map.of("mmr", mmr);
        final ResponseEntity<PlayerResponse> responseEntity = restTemplate.postForEntity(url, requestBody, PlayerResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final PlayerResponse playerResponse = responseEntity.getBody();
        final RankedPlayer rankedPlayer = playerResponse.getRankedPlayer();
        assertEquals(expectedRank, rankedPlayer.getRank());
        assertEquals(mmr, rankedPlayer.getPlayer().getMmr());
        assertEquals(expectedTier, rankedPlayer.getPlayer().getTier());
    }

    void updatePlayerAndVerify(final UpdatePlayerRequest updatePlayerRequest, final int expectedRank, final int expectedMMR, final Tier expectedTier) {
        // given
        final String url = getEndpointURL("player/update");

        // when
        final ResponseEntity<PlayerResponse> responseEntity = restTemplate.postForEntity(url, updatePlayerRequest, PlayerResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final PlayerResponse playerResponse = responseEntity.getBody();
        final RankedPlayer rankedPlayer = playerResponse.getRankedPlayer();
        assertEquals(expectedRank, rankedPlayer.getRank());
        assertEquals(expectedMMR, rankedPlayer.getPlayer().getMmr());
        assertEquals(expectedTier, rankedPlayer.getPlayer().getTier());
    }

    void deletePlayer(final long playerID) {
        // given
        final String url = getEndpointURL(String.format("player/%d", playerID));

        // when
        restTemplate.delete(url);
    }

    private String getEndpointURL(final String subURL) {
        return String.format("http://localhost:%d/dashboard/v1/%s", port, subURL);
    }
}
