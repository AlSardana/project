package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService{
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private Integer calculateLevel(Player player) {
        Integer level = (int)((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        return level;
    }
    private Integer calculateUntilNextLevel(Player player) {
        Integer untilNextLevel = 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
        return untilNextLevel;
    }
    private Boolean isThisCorrectId(Long id){
        return id != null &&
                id > 0 &&
                id == Math.floor(id);
    }
    @Override
    public List<Player> getPlayersList(String name, String title, Race race, Profession profession,
                                       Long after, Long before, Boolean banned, Integer minExperience,
                                       Integer maxExperience, Integer minLevel, Integer maxLevel) {
        List<Player> sortAllPlayers = playerRepository.findAll();
        if (name != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (title != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getTitle().contains(title))
                    .collect(Collectors.toList());
        }
        if (race != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getRace().equals(race))
                    .collect(Collectors.toList());
        }
        if (profession != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getProfession().equals(profession))
                    .collect(Collectors.toList());
        }
        if (after != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().after(new Date(after)))
                    .collect(Collectors.toList());
        }
        if (before != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().before(new Date(before)))
                    .collect(Collectors.toList());
        }
        if (banned != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.isBanned().equals(banned))
                    .collect(Collectors.toList());
        }
        if (minExperience != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience()>=(minExperience))
                    .collect(Collectors.toList());
        }
        if (maxExperience != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience()<=(maxExperience))
                    .collect(Collectors.toList());
        }
        if (minLevel != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel()>=(minLevel))
                    .collect(Collectors.toList());
        }
        if (maxLevel != null){
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel()<=(maxLevel))
                    .collect(Collectors.toList());
        }
        return sortAllPlayers;
    }

    @Override
    public List<Player> getPlayersForPage(List<Player> sortAllPlayers, PlayerOrder order,
                                          Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        return sortAllPlayers.stream()
                .sorted(getComparator(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    @Override
    public Player createNewPlayer(Player player) {
        if (player.getName()==null
                ||player.getName().isEmpty()
            || player.getTitle()==null
                ||player.getTitle().isEmpty()
            || player.getRace()==null
            || player.getProfession()==null
            || player.getExperience()==null
            || player.getBirthday()==null){
            throw new BadRequestException("Please add all required fields");
        }

        if (player.getName().length()<1 || player.getName().length()>12
            || player.getTitle().length()<1 || player.getTitle().length()>30
            || player.getExperience()<0 || player.getExperience()>10000000){
            throw new BadRequestException("Please check all required fields");
        }

        Calendar date = Calendar.getInstance();
        date.setTime(player.getBirthday());
        int year = date.get(Calendar.YEAR);
        if (year < 2000 || year > 3000){
            throw new BadRequestException("Please check birthday");
        }

        if (player.isBanned() == null){
            player.setBanned(false);
        }
        Integer level = calculateLevel(player);
        player.setLevel(level);
        Integer untilNextLevel = calculateUntilNextLevel(player);
        player.setUntilNextLevel(untilNextLevel);
        
        return playerRepository.saveAndFlush(player);
        
    }
    @Override
    public Player getPlayerById(Long id) {

        if (!playerRepository.existsById(id)){
            throw new PlayerNotFoundException("Player is not found");
        }
        return playerRepository.findById(id).get();
    }

    @Override
    public Player editPlayer(Player changes, Long id) {
        if (!isThisCorrectId(id)){
            throw new BadRequestException("Id is not correct");
        }
        if (!playerRepository.findById(id).isPresent()){
           throw new PlayerNotFoundException("Id is not correct");
        }

        Player changedPlayer = getPlayerById(id);

        String editName = changes.getName();
        if (editName != null){
            if (editName.length() < 1 || editName.length() > 12){ throw new BadRequestException("Check name");}
            changedPlayer.setName(editName);
        }
        String editTitle = changes.getTitle();
        if (editTitle != null){
            if (editTitle.length() < 1 || editTitle.length() > 30) throw new BadRequestException("Check title");
            changedPlayer.setTitle(editTitle);
        }
        if (changes.getRace()!=null){
            changedPlayer.setRace(changes.getRace());
        }
        if (changes.getProfession()!=null){
            changedPlayer.setProfession(changes.getProfession());
        }
        Integer editExperience = changes.getExperience();
        if (editExperience != null){
            if (editExperience < 0 || editExperience > 10000000) throw new BadRequestException();
            changedPlayer.setExperience(editExperience);
        }

        if (changes.getBirthday() != null){
            Calendar date = Calendar.getInstance();
            date.setTime(changes.getBirthday());
            int year = date.get(Calendar.YEAR);
            if (year < 2000 || year > 3000){
                throw new BadRequestException("Please check birthday");
            }

            changedPlayer.setBirthday(changes.getBirthday());
        }

        if (changes.isBanned()!= null){
            changedPlayer.setBanned(changes.isBanned());
        }

        Integer level = calculateLevel(changedPlayer);
        changedPlayer.setLevel(level);
        Integer untilNextLevel = calculateUntilNextLevel(changedPlayer);
        changedPlayer.setUntilNextLevel(untilNextLevel);
        return playerRepository.saveAndFlush(changedPlayer);
    }

    @Override
    public void deletePlayerById(Long id) {
        if (playerRepository.existsById(id)){
            playerRepository.deleteById(id);
        }
        else{
            throw new PlayerNotFoundException("Player is not found");
        }
    }


    private Comparator<Player> getComparator(PlayerOrder order) {
        if (order == null){
            return Comparator.comparing(Player :: getId);
        }
        Comparator<Player> comparator = null;
        switch (order.getFieldName()){
            case "id":
                comparator = Comparator.comparing(Player :: getId);
            case "birthday":
                comparator = Comparator.comparing(Player :: getBirthday);
            case "experience":
                comparator = Comparator.comparing(Player :: getExperience);
            case "level":
                comparator = Comparator.comparing(Player :: getLevel);
        }
        return comparator;
    }

}
