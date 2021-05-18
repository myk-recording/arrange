package com.example.myk.jzq_mvc.model;

import static com.example.myk.jzq_mvc.model.Player.O;
import static com.example.myk.jzq_mvc.model.Player.X;

public class Board {
    
    private Cell[][] cells = new Cell[3][3];
    private Player winner;
    private GameState state;
    private Player currentTurn;
    
    public Board() {
        restart();
    }

    public void restart() {
        clearCells();
        winner = null;
        currentTurn = X;
        state = GameState.IN_PROGRESS;
    }

    public Player mark(int row,int col) {
        Player playerMoved = null;
        if (isValid(row,col)) {
            cells[row][col].setPlayer(currentTurn);
            playerMoved = currentTurn;
            if (isWinningMove(currentTurn,row,col)) {
                state = GameState.FINISHED;
                winner = currentTurn;
            } else {
                flieCurrentTurn();
            }
        }

        return playerMoved;
    }

    public Player getWinner() {
        return winner;
    }

    private void flieCurrentTurn() {
        currentTurn = currentTurn == X ? O : X;
    }

    private boolean isWinningMove(Player currentTurn, int row, int col) {
        return (cells[row][0].getPlayer() == currentTurn
                && cells[row][1].getPlayer() == currentTurn
                && cells[row][2].getPlayer() == currentTurn
                || cells[0][col].getPlayer() == currentTurn
                && cells[1][col].getPlayer() == currentTurn
                && cells[2][col].getPlayer() == currentTurn
                || row == col
                && cells[0][0].getPlayer() == currentTurn
                && cells[1][1].getPlayer() == currentTurn
                && cells[2][2].getPlayer() == currentTurn
                || row + col == 2
                && cells[0][2].getPlayer() == currentTurn
                && cells[1][1].getPlayer() == currentTurn
                && cells[2][0].getPlayer() == currentTurn);
    }

    private boolean isValid(int row, int col) {
        if (state == GameState.FINISHED) {
            return false;
        } else if (isOutOfBounds(row) || isOutOfBounds(col)) {
            return false;
        } else if (isAlreadySet(row,col)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isAlreadySet(int row, int col) {
        return cells[row][col].getPlayer() != null;
    }

    private boolean isOutOfBounds(int row) {
        return row < 0 || row > 2;
    }

    private void clearCells() {
    }

}
