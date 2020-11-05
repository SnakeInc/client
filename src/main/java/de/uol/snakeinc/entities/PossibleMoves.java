package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public abstract class PossibleMoves {

    public static void main(String[] Args) {
        var players = new Player[] { null,
            new Player(1, 10, 10 , Direction.UP, 5, true, "1"),
            new Player(2, 10, 30 , Direction.UP, 5, true, "2"),
            new Player(3, 30, 10 , Direction.UP, 5, true, "3"),
            new Player(4, 30, 30 , Direction.UP, 5, true, "4"),
        };

        var board = new Board(40, 40, players, 1);

        var start = System.currentTimeMillis();
        var elems =
        possibleBoards(board)
            .map(Map.Entry::getKey)
            .flatMap(PossibleMoves::possibleBoards)
            //.map(Map.Entry::getKey)
            //.flatMap(PossibleMoves::possibleBoards)
            //.map(Map.Entry::getKey)
            //.flatMap(PossibleMoves::possibleBoards)
            //.map(Map.Entry::getKey)
            //.flatMap(PossibleMoves::possibleBoards)
            //.map(Map.Entry::getKey)
            //.flatMap(PossibleMoves::possibleBoards)
            .count();
        var end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(elems);

    }


    static public EntryStream<PlayerMove, Player> getPotentialMovedPlayers(Player player) {
        return StreamEx.of(PlayerMove.CHANGE_NOTHING, PlayerMove.SLOW_DOWN, PlayerMove.SPEED_UP,
            PlayerMove.TURN_LEFT, PlayerMove.TURN_RIGHT)
            .mapToEntry(move -> actOnMove(player, move))
            .nonNullValues();
    }

    static public Player actOnMove(Player player, PlayerMove move) {
        Player res = null;
        switch (move) {
            case CHANGE_NOTHING:
                res = Player.get();
                res.setDirection(player.getDirection());
                res.setSpeed(player.getSpeed());
                break;
            case SPEED_UP:
                if (player.getSpeed() > 9) {
                    return null;
                }
                res = Player.get();
                res.setSpeed(player.getSpeed() + 1);
                res.setDirection(player.getDirection());
                break;
            case SLOW_DOWN:
                if (player.getSpeed() < 2) {
                    return null;
                }
                res = Player.get();
                res.setDirection(player.getDirection());
                res.setSpeed(player.getSpeed() - 1);
                break;
            case TURN_LEFT:
            case TURN_RIGHT:
                res = Player.get();
                res.setDirection(player.getDirection().change(move));
                res.setSpeed(player.getSpeed());
                break;
        }
        res.setId(player.getId());
        res.setX(player.getX());
        res.setY(player.getY());
        res.setActive(true);
        res.setName(player.getName());
        return res;
    }

    static public boolean tryMove(Player player, Board board) {
        int x = player.getX();
        int y = player.getY();
        int speed = player.getSpeed();
        if (board.isJumpTurn()) {
            if (speed == 1) {
                switch (player.getDirection()) {
                    case RIGHT:
                        return board.isFree(x + 1, y);
                    case LEFT:
                        return board.isFree(x - 1, y);
                    case UP:
                        return board.isFree(x, y + 1);
                    case DOWN:
                        return board.isFree(x, y - 1);
                    case INVALID:
                        return false;
                }
            }
            else {
                switch (player.getDirection()) {
                    case RIGHT:
                        return board.isFree(x + 1, y) && board.isFree(x + speed, y);
                    case LEFT:
                        return board.isFree(x - 1, y) && board.isFree(x - speed, y);
                    case UP:
                        return board.isFree(x, y + 1) && board.isFree(x, y + speed);
                    case DOWN:
                        return board.isFree(x, y - 1) && board.isFree(x, y - speed);
                    case INVALID:
                        return false;
                }
            }
        } else {
            switch (player.getDirection()) {
                case RIGHT:
                    for (int it = x + speed; it > x; it--) {
                        if (! board.isFree(it, y )) {
                            return false;
                        }
                    }
                    return true;
                case LEFT:
                    for (int it = x - speed; it < x; it++) {
                        if (!board.isFree(it, y )) {
                            return false;
                        }
                    }
                    return true;
                case UP:
                    for (int it = y + speed; it > y; it--) {
                        if (! board.isFree(x, it )) {
                            return false;
                        }
                    }
                    return true;
                case DOWN:
                    for (int it = y - speed; it < y; it++) {
                        if (! board.isFree(x, it )) {
                            return false;
                        }
                    }
                    return true;
                case INVALID:
                    return false;
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    private static class IntTriple implements Comparable<IntTriple> {
        public final int x;
        public final int y;
        public final int id;

        @Override
        public int compareTo(IntTriple o) {
            int compare = Integer.compare(x, o.x);
            if (compare == 0) {
                compare = Integer.compare(y, o.y);
                if (compare == 0) {
                    compare = Integer.compare(id, o.id);
                }
            }
            return compare;
        }
    }

    static private void addCell(int x, int y, int id, ArrayList<IntTriple> list, Player player, Board board) {
        if (board.isFree(x, y)) {
            list.add(new IntTriple(x, y, id));
        } else {
            if (x >= 0 && x < board.getCells().length && y >= 0 && y < board.getCells().length) {
                list.add(new IntTriple(x,y, -1));
            }
            player.setActive(false);
        }
    }

    static public Map.Entry<Board, PlayerMove[]> actOnMoves(Iterator<Map.Entry<PlayerMove, Player>> moves, Board board) {
        var resBoard = new Board(board);
        var resMoves = new PlayerMove[board.getPlayers().length];
        Arrays.fill(resMoves, PlayerMove.INVALID);
        var newCells = new ArrayList<IntTriple>();
        while (moves.hasNext()) {
            var entry = moves.next();
            var player = entry.getValue();
            int id = player.getId();
            resBoard.getPlayers()[id] = player;
            resMoves[id] = entry.getKey();
            int speed = player.getSpeed();
            int x = player.getX();
            int y = player.getY();
            if (board.isJumpTurn()) { // the turn count has already been increased in res
                if (speed == 1) {
                    switch (player.getDirection()) {
                        case RIGHT:
                            addCell(x + 1, y, id, newCells, player, board);
                            break;
                        case LEFT:
                            addCell(x - 1, y, id, newCells, player, board);
                            break;
                        case UP:
                            addCell(x, y + 1, id, newCells, player, board);
                            break;
                        case DOWN:
                            addCell(x, y - 1, id, newCells, player, board);
                            break;
                        case INVALID:
                            throw new IllegalArgumentException();
                    }
                }
                else {
                    switch (player.getDirection()) {
                        case RIGHT:
                            addCell(x + 1, y, id, newCells, player, board);
                            addCell(x + speed, y, id, newCells, player, board);
                            break;
                        case LEFT:
                            addCell(x - 1, y, id, newCells, player, board);
                            addCell(x - speed, y, id, newCells, player, board);
                            break;
                        case UP:
                            addCell(x, y + 1, id, newCells, player, board);
                            addCell(x, y + speed, id, newCells, player, board);
                            break;
                        case DOWN:
                            addCell(x, y - 1, id, newCells, player, board);
                            addCell(x, y - speed, id, newCells, player, board);
                            break;
                        case INVALID:
                            throw new IllegalArgumentException();
                    }
                }
            } else {
                switch (player.getDirection()) {
                    case RIGHT:
                        for (int it = x + speed; it > x; it--) {
                            addCell(it, y, id, newCells, player, board);
                        }
                        break;
                    case LEFT:
                        for (int it = x - speed; it < x; it++) {
                            addCell(it, y, id, newCells, player, board);
                        }
                        break;
                    case UP:
                        for (int it = y + speed; it > y; it--) {
                            addCell(x, it, id, newCells, player, board);
                        }
                        break;
                    case DOWN:
                        for (int it = y - speed; it < y; it++) {
                            addCell(x, it, id, newCells, player, board);
                        }
                        break;
                    case INVALID:
                        throw new IllegalArgumentException();
                }
            }
        }

        Collections.sort(newCells);
        var newCellsIt = newCells.iterator();
        IntTriple last = newCellsIt.next();
        boolean lastBlocked = false;
        if (newCellsIt.hasNext()) {
            IntTriple next = newCellsIt.next();
            if (last.x == next.x && last.y == next.y) {
                if (last.id > 0) {
                    resBoard.getPlayer(last.id).setActive(false);
                }
                if (next.id > 0) {
                    resBoard.getPlayer(next.id).setActive(false);
                }
                resBoard.getCells()[last.x][last.y] = -1;
                lastBlocked = true;
            } else {
                resBoard.getCells()[last.x][last.y] = last.id;
            }
            while (newCellsIt.hasNext()) {
                last = next;
                next = newCellsIt.next();
                if (last.x == next.x && last.y == next.y) {
                    if (last.id  > 0) {
                        resBoard.getPlayer(last.id).setActive(false);
                    }
                    if (next.id  > 0) {
                        resBoard.getPlayer(next.id).setActive(false);
                    }
                    resBoard.getCells()[last.x][last.y] = -1;
                    lastBlocked = true;
                } else {
                    if( !lastBlocked) {
                        resBoard.getCells()[last.x][last.y] = last.id;
                    } else {
                        lastBlocked = false;
                    }
                }
            }
        }

        return Map.entry(resBoard, resMoves);
    }

    static public StreamEx<Map.Entry<Board, PlayerMove[]>> possibleBoards(Board board) {
        var combinations = new CombinationTree();
        StreamEx.of(board.getPlayers())
            .nonNull()
            .filter(Player::isActive)
            .map(PossibleMoves::getPotentialMovedPlayers)
            .forEach(combinations::add);

        return combinations.getCombinationsStreamEx()
            .map(combination -> actOnMoves(combination, board));
    }

}
