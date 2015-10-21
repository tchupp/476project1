package edu.msu.chuppthe.steampunked;

public class Player {

    private String playerName;

    private StartingPipe startingPipe;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getName() {
        return playerName;
    }

    public StartingPipe getStartingPipe() {
        return startingPipe;
    }

    public void setStartingPipe(StartingPipe startingPipe) {
        this.startingPipe = startingPipe;
        startingPipe.setPlayer(this);
    }

    public void setActive(Boolean active) {
        this.startingPipe.setActive(active);
    }
}
