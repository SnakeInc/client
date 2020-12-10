package de.uol.snakeinc.entities;

import lombok.CustomLog;

@CustomLog
public class JumpCounter {

    Player[] players;
    int[][] speed;
    Player pseudoPlayer;

    public JumpCounter(Player[] players) {
        this.players = players;
        speed = new int[players.length][2];
        for (int i = 0; i < players.length; i++) {
            speed[i][0] = players[i].getSpeed();
            speed[i][1] = 1;
        }
    }

    public JumpCounter(JumpCounter jumpCounter, Player player) {
        this.players = jumpCounter.players;
        this.speed = jumpCounter.speed;
        this.pseudoPlayer = player;
    }

    public boolean check(Player player) {
        int iD = player.getId()-1;
        if (players[iD].getSpeed() < 3) {
            return false;
        }
        if (speed[iD][0] == player.getSpeed()) {
            if (speed[iD][1] == 6) {
                speed[iD][1] = 0;
                log.info("A Hole detected!");
                return true;
            } else {
                speed[iD][1]++;
                return false;
            }
        } else {
            speed[iD][0] = player.getSpeed();
            speed[iD][1] = 1;
            return false;
        }
    }

    public boolean check() {
        int iD = pseudoPlayer.getId();
        if (players[iD].getSpeed() < 3) {
            return false;
        }
        if (speed[iD][0] == pseudoPlayer.getSpeed()) {
            if (speed[iD][1] == 6) {
                speed[iD][1] = 0;
                return true;
            } else {
                speed[iD][1]++;
                return false;
            }
        } else {
            speed[iD][0] = pseudoPlayer.getSpeed();
            speed[iD][1] = 1;
            return false;
        }
    }
}
