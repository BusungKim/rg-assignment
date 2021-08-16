package com.riot.config;

import com.riot.dao.InMemoryPlayerRepository;
import com.riot.dao.PlayerRepository;
import com.riot.model.Player;
import com.riot.model.Tier;
import com.riot.util.TierDecider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private static final String INITIAL_DATA_FILE_PATH = "classpath:static/initialData.txt";

    private final ResourceLoader resourceLoader;

    @Bean
    public PlayerRepository playerRepository() throws Exception {
        final Resource resource = resourceLoader.getResource(INITIAL_DATA_FILE_PATH);

        final List<Player> playerList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] splits = line.split(",");
                if (splits.length != 2) {
                    break;
                }
                final long playerID = Long.parseLong(splits[0]);
                final int mmr = Integer.parseInt(splits[1]);

                playerList.add(new Player().setId(playerID).setMmr(mmr));
            }
        }

        final TierDecider tierDecider = new TierDecider(playerList.size());
        playerList.sort(Comparator.reverseOrder());
        for (int i = 0; i < playerList.size(); i++) {
            final Player player = playerList.get(i);
            final Tier tier = tierDecider.getPlayerTier(i+1);
            player.setTier(tier);
        }

        Collections.shuffle(playerList); // TODO: check whether it is necessary or not to prevent worst case

        log.debug("Read {} users from file {}", playerList.size(), INITIAL_DATA_FILE_PATH);
        return new InMemoryPlayerRepository(playerList);
    }
}
