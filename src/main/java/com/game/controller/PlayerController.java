package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerServiceImpl playerServiceImpl;

    @Autowired
    public PlayerController(PlayerServiceImpl playerServiceImpl) {
        this.playerServiceImpl = playerServiceImpl;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) Race race,
                                       @RequestParam(required = false)Profession profession,
                                       @RequestParam(required = false) Long after,
                                       @RequestParam(required = false) Long before,
                                       @RequestParam(required = false) Boolean banned,
                                       @RequestParam(required = false) Integer minExperience,
                                       @RequestParam(required = false) Integer maxExperience,
                                       @RequestParam(required = false) Integer minLevel,
                                       @RequestParam(required = false) Integer maxLevel,
                                       @RequestParam(required = false) PlayerOrder order,
                                       @RequestParam(required = false) Integer pageNumber,
                                       @RequestParam(required = false) Integer pageSize){
        List<Player> players = playerServiceImpl.getPlayersList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel);

        return playerServiceImpl.getPlayersForPage(players, order, pageNumber, pageSize);
    }
    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCountSortPlayer(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) Race race,
                                      @RequestParam(required = false)Profession profession,
                                      @RequestParam(required = false) Long after,
                                      @RequestParam(required = false) Long before,
                                      @RequestParam(required = false) Boolean banned,
                                      @RequestParam(required = false) Integer minExperience,
                                      @RequestParam(required = false) Integer maxExperience,
                                      @RequestParam(required = false) Integer minLevel,
                                      @RequestParam(required = false) Integer maxLevel){
        return playerServiceImpl.getPlayersList(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel).size();
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayerById(@PathVariable Long id){
        if (Long.valueOf(id) == null || Long.valueOf(id) <= 0){
        throw new BadRequestException("Check Id");
        }
        Player playerById = playerServiceImpl.getPlayerById(Long.valueOf(id));
        if (playerById == null){
            throw new PlayerNotFoundException("Player with this Id is not found");
        }
        return playerById;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player createNewPlayer(@RequestBody Player player){
        Player createdPlayer = playerServiceImpl.createNewPlayer(player);
        if (createdPlayer == null){
            throw new BadRequestException("Player is not created");
        }
        return createdPlayer;
    }

    @PostMapping("/players/{id}")
    public Player editPlayer(@RequestBody Player changes, @PathVariable("id") Long id){
        if (Long.valueOf(id) == null || Long.valueOf(id) <= 0){
            throw new BadRequestException("Check Id");
        }

        return playerServiceImpl.editPlayer(changes, Long.valueOf(id));
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayerById(@PathVariable Long id){
        if (Long.valueOf(id) == null || Long.valueOf(id) <= 0){
            throw new BadRequestException("Check Id");
        }
        playerServiceImpl.deletePlayerById(Long.valueOf(id));
    }

}
