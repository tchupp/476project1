package edu.msu.chuppthe.steampunked.game;

public class Player {

    private String playerName;

    private StartingPipe startingPipe;
    private Pipe endingPipe;

    private boolean leak = true;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getName() {
        return playerName;
    }

    public boolean hasLeak() {
        return leak;
    }

    public void setLeak(boolean leak) {
        this.leak = leak;
    }

    public StartingPipe getStartingPipe() {
        return startingPipe;
    }

    public void setStartingPipe(StartingPipe startingPipe) {
        this.startingPipe = startingPipe;
        startingPipe.setPlayer(this);
    }

    public Pipe getEndingPipe() {
        return endingPipe;
    }

    public void setEndingPipe(Pipe endingPipe) {
        this.endingPipe = endingPipe;
        endingPipe.setPlayer(this);
    }

    public void setActive(Boolean active) {
        this.startingPipe.setActive(active);
    }
}
