package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;

public class LocalBoardPool {

    private final int us;
    private final int height;
    private final int width;
    private final int playerSize;

    private final Board[] pool;
    private int head;
    private final int size;
    public int max = 0;

    public LocalBoardPool(int poolSize, int us, int height, int width, int playerSize, int pregen) {
        pool = new Board[poolSize];
        size = poolSize;
        head = pregen - 1;

        this.us = us;
        this.height = height;
        this.width = width;
        this.playerSize = playerSize;
        for (int i = 0; i < pool.length && i < pregen; i++) {
            pool[i] = new Board(playerSize, width, height, us);
        }

    }

    public Board borrow() {
        if (head == -1) {
            return new Board(playerSize, width, height, us);
        } else {
            var res = pool[head];
            pool[head] = null;
            head--;
            if (size - head > max) {
                max = size - head;
            }
            return res;
        }
    }

    public void release(Board board) {
        if (head >= size - 1) {
            return;
        } else {
            head++;
            pool[head] = board;
        }
    }
}
