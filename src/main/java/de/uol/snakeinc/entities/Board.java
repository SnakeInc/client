package de.uol.snakeinc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class Board {


    @Getter
    int width;
    @Getter
    private int height;
    @Getter
    private int cells[][];
    @Getter
    private int turn;
    @Getter
    private Player players[];
    @Getter
    private int us;

    public Board(int width, int height, Player[] players, int us) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.turn = 1;
        this.us = us;

        cells = new int[width][height];
        for (int it = 0; it < width; it++) {
            int[] row = cells[it];
            Arrays.fill(row, 0);
        }

        for (Player player : players) {
            cells[player.getX()][player.getY()] = player.getId();
        }
    }

    public Board(Board board) {
        this.width = board.getWidth();
        this.height = board.getHeight();
        this.players = board.getPlayers();
        this.turn = board.turn + 1;

        this.cells = new int[board.getCells().length][];
        for (int it = 0; it < width; it++) {
            this.cells[it] = Arrays.copyOf(board.getCells()[it], board.getCells()[it].length);
        }
    }

    public boolean isJumpTurn() {
        return turn % 6 == 0;
    }

    public Player getPlayer(int id) {
        return players[id];
    }

    public Player getOurPlayer() {
        return getPlayer(us);
    }

    public StreamEx<Player> getActivePlayers() {
        return StreamEx.of(players).filter(Player::isActive);
    }

    public boolean isFree(int x, int y) {
        if (x < 0 || y < 0 || x >= cells.length || y >= cells.length ) {
            return false;
        }
        return cells[x][y] == 0;
    }

    public boolean tryMove(Player player) {
        int x = player.getX();
        int y = player.getY();
        int speed = player.getSpeed();
        if (isJumpTurn()) {
            if (speed == 1) {
                switch (player.getDirection()) {
                    case RIGHT:
                        return isFree(x + 1, y);
                    case LEFT:
                        return isFree(x - 1, y);
                    case UP:
                        return isFree(x, y + 1);
                    case DOWN:
                        return isFree(x, y - 1);
                    case INVALID:
                        return false;
                }
            }
            else {
                switch (player.getDirection()) {
                    case RIGHT:
                        return isFree(x + 1, y) && isFree(x + speed, y);
                    case LEFT:
                        return isFree(x - 1, y) && isFree(x - speed, y);
                    case UP:
                        return isFree(x, y + 1) && isFree(x, y + speed);
                    case DOWN:
                        return isFree(x, y - 1) && isFree(x, y - speed);
                    case INVALID:
                        return false;
                }
            }
        } else {
            switch (player.getDirection()) {
                case RIGHT:
                    for (int it = x + speed; it > x; it--) {
                        if (! isFree(it, y )) {
                            return false;
                        }
                    }
                    return true;
                case LEFT:
                    for (int it = x - speed; it < x; it++) {
                        if (!isFree(it, y )) {
                            return false;
                        }
                    }
                    return true;
                case UP:
                    for (int it = y + speed; it > y; it--) {
                        if (! isFree(x, it )) {
                            return false;
                        }
                    }
                    return true;
                case DOWN:
                    for (int it = y - speed; it < y; it++) {
                        if (! isFree(x, it )) {
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

    @Data @AllArgsConstructor
    private class IntTriple implements Comparable<IntTriple> {
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

    private void addCell(int x, int y, int id, ArrayList<IntTriple> list, Player player) {
        if (this.isFree(x, y)) {
            list.add(new IntTriple(x, y, id));
        } else {
            if (x >= 0 && x < cells.length && y >= 0 && y < cells.length) {
                list.add(new IntTriple(x,y, -1));
            }
            player.setActive(false);
        }
    }

    public Map.Entry<Board, PlayerMove> actOnMoves(Iterator<Map.Entry<Player, PlayerMove>> moves) {
        var res = new Board(this);
        PlayerMove ourMove = null;
        var newCells = new ArrayList<IntTriple>();
        while (moves.hasNext()) {
            var entry = moves.next();
            var player = entry.getKey();
            int id = player.getId();
            if (id == us) {
                ourMove = entry.getValue();
            }
            res.players[id] = player;
            int speed = player.getSpeed();
            int x = player.getX();
            int y = player.getY();
            var direction = player.getDirection();
            if (isJumpTurn()) {
                if (speed == 1) {
                    switch (player.getDirection()) {
                        case RIGHT:
                            addCell(x + 1, y, id, newCells, player);
                            break;
                        case LEFT:
                            addCell(x - 1, y, id, newCells, player);
                            break;
                        case UP:
                            addCell(x, y + 1, id, newCells, player);
                            break;
                        case DOWN:
                            addCell(x, y - 1, id, newCells, player);
                            break;
                        case INVALID:
                            throw new IllegalArgumentException();
                    }
                }
                else {
                    switch (player.getDirection()) {
                        case RIGHT:
                            addCell(x + 1, y, id, newCells, player);
                            addCell(x + speed, y, id, newCells, player);
                            break;
                        case LEFT:
                            addCell(x - 1, y, id, newCells, player);
                            addCell(x - speed, y, id, newCells, player);
                            break;
                        case UP:
                            addCell(x, y + 1, id, newCells, player);
                            addCell(x, y + speed, id, newCells, player);
                            break;
                        case DOWN:
                            addCell(x, y - 1, id, newCells, player);
                            addCell(x, y - speed, id, newCells, player);
                            break;
                        case INVALID:
                            throw new IllegalArgumentException();
                    }
                }
            } else {
                switch (player.getDirection()) {
                    case RIGHT:
                        for (int it = x + speed; it > x; it--) {
                            addCell(it, y, id, newCells, player);
                        }
                        break;
                    case LEFT:
                        for (int it = x - speed; it < x; it++) {
                            addCell(it, y, id, newCells, player);
                        }
                        break;
                    case UP:
                        for (int it = y + speed; it > y; it--) {
                            addCell(x, it, id, newCells, player);
                        }
                        break;
                    case DOWN:
                        for (int it = y - speed; it < y; it++) {
                            addCell(x, it, id, newCells, player);
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
        if (newCellsIt.hasNext()) {
            IntTriple next = newCellsIt.next();
            if (last.x == next.x && last.y == next.y) {
                if (last.id  > 0) {
                    res.getPlayer(last.id).setActive(false);
                }
                if (next.id  > 0) {
                    res.getPlayer(next.id).setActive(false);
                }
            }
            while (newCellsIt.hasNext()) {
                last = next;
                next = newCellsIt.next();
                if (last.x == next.x && last.y == next.y) {
                    if (last.id  > 0) {
                        res.getPlayer(last.id).setActive(false);
                    }
                    if (next.id  > 0) {
                        res.getPlayer(next.id).setActive(false);
                    }
                }
            }
        }

        return Map.entry(res, ourMove);
    }

    public List<Map.Entry<Board, PlayerMove>> possibleBoards() {
        var potentialMoves = getActivePlayers()
            .unordered()
            //.parallel()
            .map(player -> player.getPotentialMovedPlayers(this))
            .toList();

        var combinations = new CombinationTree(potentialMoves.get(0));
        potentialMoves.remove(0);
        potentialMoves.forEach(combinations::add);

        return combinations.getCombinations().stream()
            .unordered()
            //.parallel()
            .map(this::actOnMoves)
            .collect(Collectors.toList());
    }


    private class CombinationTree {
        List<CombinationNode> starts;
        int depth = 1;

        CombinationTree(EntryStream<Player, PlayerMove> start) {
            starts = new ArrayList<CombinationNode>();
            start.map(entry -> new CombinationNode(entry, null)).forEach(starts::add);
            depth ++;
        }

        public void add(EntryStream<Player, PlayerMove> toAdd) {
            var newStart = new ArrayList<CombinationNode>(starts.size() * 5);
            for (var node : starts) {
                for (var entry : toAdd) {
                    newStart.add(new CombinationNode(entry, node));
                }
            }
            starts = newStart;
            depth ++;
        }

        public List<Iterator<Map.Entry<Player, PlayerMove>>> getCombinations() {
            return starts.stream().map(CombinationNode::toIterator).collect(Collectors.toList());
        }

        private class CombinationNode {
            Map.Entry<Player, PlayerMove> entry;
            CombinationNode parent;

            CombinationNode(Map.Entry<Player, PlayerMove> entry, CombinationNode node) {
                this.entry = entry;
                this.parent = node;
            }

            CombinationIterator toIterator() {
                return new CombinationIterator(this);
            }

        }

        private class CombinationIterator implements Iterator<Map.Entry<Player, PlayerMove>> {
            private CombinationNode current;

            CombinationIterator(CombinationNode node) {
                this.current = node;
            }

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Map.Entry<Player, PlayerMove> next() {
                var res = current.entry;
                current = current.parent;
                return res;
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return new CombinationIterator(current);
            }
        }
    }
}
