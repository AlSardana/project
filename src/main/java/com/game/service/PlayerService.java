package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import java.util.List;

public interface PlayerService {

    List<Player> getPlayersList(String name, String title, Race race, Profession profession,
                                Long after, Long before, Boolean banned, Integer minExperience,
                                Integer maxExperience, Integer minLevel, Integer maxLevel);
    List<Player> getPlayersForPage(List<Player> sortAllPlayers, PlayerOrder order,
                                   Integer pageNumber, Integer pageSize);

    Player createNewPlayer(Player player);
    Player editPlayer(Player changes, Long id);
    void deletePlayerById(Long id);
    Player getPlayerById(Long id);
}
