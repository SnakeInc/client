package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Coordinates;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActionPlayerCoordinates {

    Action action;
    Player player;
    List<Coordinates> coordinates;
    
}
