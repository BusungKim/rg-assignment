package com.riot.dao;

import com.riot.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    int getTotalPlayerCount();
    Optional<Player> findPlayer(long playerID);
    List<Player> getRangePlayers(int maxRank, int minRank);
    void addPlayer(Player player);
    void updatePlayer(Player player);
    void deletePlayer(long playerID);
    int getRank(Player player);
    int getRank(int mmr);
    long newPlayerID();
}
