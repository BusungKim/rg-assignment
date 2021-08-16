package com.riot.dao;

import com.riot.exception.InvalidArgumentException;
import com.riot.exception.NoDataException;
import com.riot.model.Player;
import com.riot.model.Tier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

class InMemoryPlayerRepositoryTest {

    private static final List<Player> SAMPLE_INITIAL_DATA = List.of(
            createPlayer(1, 100, Tier.BRONZE),
            createPlayer(2, 200, Tier.SILVER),
            createPlayer(3, 300, Tier.GOLD),
            createPlayer(4, 400, Tier.PLATINUM),
            createPlayer(5, 500, Tier.DIAMOND),
            createPlayer(6, 600, Tier.MASTER),
            createPlayer(7, 700, Tier.CHALLENGER)
    );

    private InMemoryPlayerRepository repository;

    @BeforeEach
    void beforeEach() {
        repository = createRepository();
    }

    @Test
    void testInitialTotalCount_Success() {
        // when
        final int totalCount = repository.getTotalPlayerCount();

        // then
        Assertions.assertEquals(7, totalCount);
    }

    @Test
    void testFindPlayer_Success() {
        // when
        final Player player = repository.findPlayer(1).get();

        // then
        Assertions.assertEquals(createPlayer(1, 100, Tier.BRONZE), player);
    }

    @Test
    void testFindPlayer_WhenNoPlayerExist_ReturnEmptyOptional_Success() {
        // when
        final Optional<Player> player = repository.findPlayer(123);

        // then
        Assertions.assertTrue(player.isEmpty());
    }

    @Test
    void testGetRangePlayers_Success() {
        // when
        final List<Player> players = repository.getRangePlayers(3, 5);

        // then
        Assertions.assertEquals(List.of(
                createPlayer(5, 500, Tier.DIAMOND),
                createPlayer(4, 400, Tier.PLATINUM),
                createPlayer(3, 300, Tier.GOLD)), players);
    }

    @Test
    void testGetRangePlayers_RangeExceeds_Success() {
        // when
        final List<Player> players = repository.getRangePlayers(1, 1000);

        // then
        Assertions.assertEquals(7, players.size());
    }

    @Test
    void testGetRangePlayers_InvalidRange_Error() {
        // when & then
        Assertions.assertThrows(InvalidArgumentException.class, () -> repository.getRangePlayers(5, 1));
    }

    @Test
    void testAddPlayer_Success() {
        // when
        final Player newPlayer = new Player().setId(8).setMmr(1000).setTier(Tier.CHALLENGER);
        repository.addPlayer(newPlayer);

        // then
        Assertions.assertEquals(8, repository.getTotalPlayerCount());
        Assertions.assertEquals(1, repository.getRank(newPlayer));
    }

    @Test
    void testUpdatePlayer_WhenPlayerIsDemoted_Success() {
        // when
        final Player demotedPlayer = new Player().setId(7).setMmr(700).setTier(Tier.DIAMOND);
        repository.updatePlayer(demotedPlayer);

        // then
        Assertions.assertEquals(7, repository.getTotalPlayerCount());
        Assertions.assertEquals(2, repository.getRank(demotedPlayer));
    }

    @Test
    void testUpdatePlayer_CannotFindPlayer_Error() {
        // when & then
        final Player updatedPlayer = new Player().setId(8).setMmr(700).setTier(Tier.DIAMOND);
        Assertions.assertThrows(NoDataException.class, () -> repository.updatePlayer(updatedPlayer));
    }

    @Test
    void testGetRank_Success() {
        // when
        final Player player = createPlayer(3, 300, Tier.GOLD);
        final int rank = repository.getRank(player);

        // then
        Assertions.assertEquals(5, rank);
    }

    @Test
    void testGetRank_EstimateUnregisteredPlayerRank_Success() {
        // when
        final int rank = repository.getRank(1000);

        // then
        Assertions.assertEquals(1, rank);
    }

    @Test
    void testNewPlayerID_ConcurrentExecution_Success() {
        // when
        IntStream.range(0, 10).parallel().forEach(ignored -> repository.newPlayerID());

        // then
        Assertions.assertEquals(18, repository.newPlayerID());
    }

    private static Player createPlayer(final long id, final int mmr, final Tier tier) {
        return new Player().setId(id).setMmr(mmr).setTier(tier);
    }

    private static InMemoryPlayerRepository createRepository() {
        return new InMemoryPlayerRepository(SAMPLE_INITIAL_DATA);
    }
}
