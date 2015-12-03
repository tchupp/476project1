package edu.msu.chuppthe.steampunked.game;

public class GameInfo {
    private String gameName;
    private String playerOneName;
    private String playerTwoName;
    private int gridSize;

    public GameInfo(String gameName, String playerOneName, String playerTwoName, int gridSize) {
        this.gameName = gameName;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.gridSize = gridSize;
    }

    public String getGameName() {
        return this.gameName;
    }

    public String getPlayerOneName() {
        return this.playerOneName;
    }

    public String getPlayerTwoName() {
        return this.playerTwoName;
    }

    public int getGridSize() {
        return this.gridSize;
    }

}
