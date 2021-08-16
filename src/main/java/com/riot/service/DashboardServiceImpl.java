package com.riot.service;

import com.riot.dao.PlayerRepository;
import com.riot.exception.InvalidArgumentException;
import com.riot.exception.NoDataException;
import com.riot.model.Player;
import com.riot.model.RankedPlayer;
import com.riot.model.Tier;
import com.riot.model.request.AddPlayerRequest;
import com.riot.model.request.UpdatePlayerRequest;
import com.riot.model.response.NearPlayersResponse;
import com.riot.model.response.PlayerCountResponse;
import com.riot.model.response.PlayerResponse;
import com.riot.model.response.TierResponse;
import com.riot.model.response.TopPlayersResponse;
import com.riot.util.TierDecider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PlayerRepository playerRepository;

    @Override
    public PlayerCountResponse getTotalUserCount() {
        return new PlayerCountResponse().setTotalCountOfPlayers(playerRepository.getTotalPlayerCount());
    }

    @Override
    public TierResponse getTierOfPlayer(final long playerID) {
        final Player player = getPlayer(playerID);
        return new TierResponse().setTier(player.getTier());
    }

    @Override
    public TopPlayersResponse getTopPlayers(final int count) {
        if (count <= 0) {
            throw new InvalidArgumentException(String.format("count(%d) must be positive value", count));
        }

        final List<Player> players = playerRepository.getRangePlayers(1, count);
        final List<RankedPlayer> rankedPlayers = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(players.get(i)).setRank(i+1);
            rankedPlayers.add(rankedPlayer);
        }
        return new TopPlayersResponse().setCount(players.size()).setPlayers(rankedPlayers);
    }

    @Override
    public NearPlayersResponse getNearPlayers(final long pivotPlayerID, final int range) {
        if (range < 0) {
            throw new InvalidArgumentException(String.format("range(%d) must be greater than or equal to 0", range));
        }

        final Player player = getPlayer(pivotPlayerID);
        final int pivotRank = playerRepository.getRank(player);

        final int maxRank = Math.max(1, pivotRank - range);
        final int minRank = Math.min(playerRepository.getTotalPlayerCount(), pivotRank + range);

        final List<Player> rangePlayers = playerRepository.getRangePlayers(maxRank, minRank);
        final List<RankedPlayer> rankedPlayers = new ArrayList<>();

        for (int i = 0; i < rangePlayers.size(); i++) {
            final RankedPlayer rankedPlayer = new RankedPlayer().setPlayer(rangePlayers.get(i)).setRank(maxRank + i);
            rankedPlayers.add(rankedPlayer);
        }

        return new NearPlayersResponse().setCount(rankedPlayers.size())
                .setMaxRank(maxRank)
                .setMinRank(minRank)
                .setPivotPlayerID(pivotPlayerID)
                .setNearPlayers(rankedPlayers);
    }

    @Override
    public PlayerResponse addPlayer(@NonNull final AddPlayerRequest addPlayerRequest) {
        final long newPlayerID = playerRepository.newPlayerID();
        final Tier tier = getTier(addPlayerRequest.getMmr());

        final Player player = new Player().setId(newPlayerID).setMmr(addPlayerRequest.getMmr()).setTier(tier);
        playerRepository.addPlayer(player);

        return new PlayerResponse().setPlayer(convertToRankedPlayer(player));
    }

    @Override
    public PlayerResponse updatePlayer(@NonNull final UpdatePlayerRequest updatePlayerRequest) {
        final Player player = getPlayer(updatePlayerRequest.getPlayerID());

        if (updatePlayerRequest.getMmr() != null) {
            player.setMmr(updatePlayerRequest.getMmr());
        }

        if (updatePlayerRequest.getTier() == null) {
            final Tier tier = getTier(updatePlayerRequest.getMmr());
            player.setTier(tier);
        } else {
            player.setTier(updatePlayerRequest.getTier());
        }

        playerRepository.updatePlayer(player);

        return new PlayerResponse().setPlayer(convertToRankedPlayer(player));
    }

    @Override
    public void deletePlayer(final long playerID) {
        playerRepository.deletePlayer(playerID);
    }

    private Player getPlayer(final long playerID) {
        return playerRepository.findPlayer(playerID)
                .orElseThrow(() -> new NoDataException(String.format("Cannot find player %d.", playerID)));
    }

    private Tier getTier(final int mmr) {
        final int rank = playerRepository.getRank(mmr);
        final TierDecider tierDecider = new TierDecider(playerRepository.getTotalPlayerCount());

        return tierDecider.getPlayerTier(rank);
    }

    private RankedPlayer convertToRankedPlayer(final Player player) {
        final int rank = playerRepository.getRank(player);
        return new RankedPlayer().setPlayer(player).setRank(rank);
    }
}
