package com.riot.dao;

import com.riot.exception.InvalidArgumentException;
import com.riot.model.Player;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryPlayerRepository implements PlayerRepository {

    private final SortedSet<Player> players;
    private final AtomicLong nextPlayerID;

    public InMemoryPlayerRepository(final List<Player> players) {
        this.players = new ConcurrentSkipListSet<>(players).descendingSet();

        final long maxPlayerID = players.stream().map(Player::getId).max(Long::compareTo).get();
        this.nextPlayerID = new AtomicLong(maxPlayerID + 1);
    }

    @Override
    public int getTotalPlayerCount() {
        return players.size();
    }

    @Override
    public Optional<Player> findPlayer(final long playerID) {
        return players.stream().filter(player -> player.getId() == playerID).findFirst();
    }

    @Override
    public List<Player> getRangePlayers(final int maxRank, final int minRank) {
        if (maxRank > minRank) {
            throw new InvalidArgumentException(String.format("maxRank(%d) must be less than or equal to minRank(%d)", maxRank, minRank));
        }

        return players.stream()
                .skip(maxRank - 1)
                .limit(minRank - maxRank + 1)
                .collect(Collectors.toList());
    }

    @Override
    public void addPlayer(@NonNull final Player player) {
        players.add(player);
    }

    @Override
    public void updatePlayer(@NonNull final Player player) {
        deletePlayer(player.getId());
        players.add(player);
    }

    @Override
    public void deletePlayer(final long playerID) {
        players.removeIf(p -> p.getId() == playerID);
    }

    @Override
    public int getRank(@NonNull final Player player) {
        return players.headSet(player).size() + 1;
    }

    @Override
    public int getRank(final int mmr) {
        return (int) players.stream().filter(player -> player.getMmr() >= mmr).count() + 1;
    }

    @Override
    public long newPlayerID() {
        return nextPlayerID.getAndIncrement();
    }
}
