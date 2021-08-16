package com.riot.service;

import com.riot.dao.PlayerRepository;
import com.riot.exception.InvalidArgumentException;
import com.riot.exception.NoDataException;
import com.riot.model.Player;
import com.riot.model.RankedPlayer;
import com.riot.model.Tier;
import com.riot.model.request.AddPlayerRequest;
import com.riot.model.request.UpdatePlayerRequest;
import com.riot.model.response.PlayerCountResponse;
import com.riot.model.response.PlayerResponse;
import com.riot.model.response.TierResponse;
import com.riot.model.response.TopPlayersResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private DashboardServiceImpl service;

    @Test
    void testGetTotalUserCount_Success() {
        // given
        when(playerRepository.getTotalPlayerCount()).thenReturn(100);

        // when
        final PlayerCountResponse ret = service.getTotalUserCount();

        // then
        final PlayerCountResponse expected = new PlayerCountResponse().setTotalCountOfPlayers(100);
        assertEquals(expected, ret);
    }

    @Test
    void testGetTierOfPlayer_Success() {
        // given
        final Player player = new Player().setId(1).setMmr(100).setTier(Tier.PLATINUM);
        when(playerRepository.findPlayer(anyLong())).thenReturn(Optional.of(player));

        // when
        final TierResponse ret = service.getTierOfPlayer(1);

        // then
        final TierResponse expected = new TierResponse().setTier(Tier.PLATINUM);
        assertEquals(expected, ret);
    }

    @Test
    void testGetTierOfPlayer_NoPlayerExistWithGivenID_Success() {
        // given
        when(playerRepository.findPlayer(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoDataException.class, () -> service.getTierOfPlayer(1));
    }

    @Test
    void testGetTopPlayers_Success() {
        // given
        final Random random = new Random(System.currentTimeMillis());
        final List<Player> players = LongStream.range(0, 100)
                .boxed().map(i -> new Player().setId(i).setMmr(random.nextInt(3000))).sorted()
                .collect(Collectors.toList());
        Collections.reverse(players);

        when(playerRepository.getRangePlayers(1, 100)).thenReturn(players);

        // when
        final TopPlayersResponse ret = service.getTopPlayers(100);

        // then
        final List<RankedPlayer> rankedPlayers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(players.get(i)).setRank(i+1);
            rankedPlayers.add(rankedPlayer);
        }

        assertEquals(new TopPlayersResponse().setCount(100).setPlayers(rankedPlayers), ret);
    }

    @Test
    void testGetTopPlayers_CountIsInvalid_Error() {
        // when & then
        assertThrows(InvalidArgumentException.class, () -> service.getTopPlayers(0));
    }

    @Test
    void testGetNearPlayers_CalculateProperRange_Success() {
        // given
        final Player pivotPlayer = new Player().setId(1).setMmr(1000).setTier(Tier.SILVER);
        when(playerRepository.findPlayer(anyLong())).thenReturn(Optional.of(pivotPlayer));
        when(playerRepository.getRank(pivotPlayer)).thenReturn(2);
        when(playerRepository.getTotalPlayerCount()).thenReturn(10);

        // when
        service.getNearPlayers(1, 3);

        // then
        verify(playerRepository).getRangePlayers(1, 5);
    }

    @Test
    void testAddPlayer_Success() {
        // given
        final long playerID = 123456789L;
        final int mmr = 1000;
        final int totalPlayerCount = 1_000_000;
        final int playerRank = 500_000;

        when(playerRepository.newPlayerID()).thenReturn(playerID);
        when(playerRepository.getTotalPlayerCount()).thenReturn(totalPlayerCount);
        when(playerRepository.getRank(mmr)).thenReturn(playerRank);
        when(playerRepository.getRank(any(Player.class))).thenReturn(playerRank);

        // when
        final AddPlayerRequest addPlayerRequest = new AddPlayerRequest().setMmr(mmr);
        final PlayerResponse ret = service.addPlayer(addPlayerRequest);

        // then
        final Player addedPlayer = new Player().setId(playerID).setMmr(mmr).setTier(Tier.SILVER);
        verify(playerRepository).addPlayer(addedPlayer);

        final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(addedPlayer).setRank(playerRank);
        final PlayerResponse expected = new PlayerResponse().setPlayer(rankedPlayer);

        assertEquals(expected, ret);
    }

    @Test
    void testUpdatePlayer_UpdateMMROnly_Success() {
        // given
        final long playerID = 123456789L;
        final int originalMMR = 1000;
        final int updatedMMR = 1050;
        final Tier originalTier = Tier.SILVER;

        final Player player = spy(new Player().setId(playerID).setMmr(originalMMR).setTier(originalTier));
        when(playerRepository.findPlayer(playerID)).thenReturn(Optional.of(player));
        when(playerRepository.getRank(updatedMMR)).thenReturn(200_000);
        when(playerRepository.getRank(player)).thenReturn(200_000);
        when(playerRepository.getTotalPlayerCount()).thenReturn(1_000_000);

        final UpdatePlayerRequest updatePlayerRequest = new UpdatePlayerRequest().setPlayerID(playerID).setMmr(updatedMMR);

        // when
        final PlayerResponse ret = service.updatePlayer(updatePlayerRequest);

        // then
        verify(player).setMmr(updatedMMR);
        verify(player).setTier(Tier.GOLD);
        verify(playerRepository).updatePlayer(player);

        final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(player).setRank(200_000);
        assertEquals(new PlayerResponse().setPlayer(rankedPlayer), ret);
    }

    @Test
    void testUpdatePlayer_UpdateTierOnly_Success() {
        // given
        final long playerID = 123456789L;
        final int originalMMR = 1000;

        final Player player = spy(new Player().setId(playerID).setMmr(originalMMR).setTier(Tier.MASTER));
        when(playerRepository.findPlayer(playerID)).thenReturn(Optional.of(player));
        when(playerRepository.getRank(player)).thenReturn(10);

        final UpdatePlayerRequest updatePlayerRequest = new UpdatePlayerRequest().setPlayerID(playerID).setTier(Tier.CHALLENGER);

        // when
        final PlayerResponse ret = service.updatePlayer(updatePlayerRequest);

        // then
        verify(player).setTier(Tier.CHALLENGER);
        verify(playerRepository).updatePlayer(player);

        final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(player).setRank(10);
        assertEquals(new PlayerResponse().setPlayer(rankedPlayer), ret);
    }

    @Test
    void testUpdatePlayer_PlayerNotExist_Error() {
        // given
        final long playerID = 123456789L;

        when(playerRepository.findPlayer(playerID)).thenReturn(Optional.empty());

        final UpdatePlayerRequest updatePlayerRequest = new UpdatePlayerRequest().setPlayerID(playerID).setTier(Tier.CHALLENGER);

        // when & then
        assertThrows(NoDataException.class, () -> service.updatePlayer(updatePlayerRequest));
    }

    @Test
    void testDeletePlayer_Success() {
        // given
        final long playerID = 123456789L;

        // when
        service.deletePlayer(playerID);

        // then
        verify(playerRepository).deletePlayer(playerID);
    }
}
