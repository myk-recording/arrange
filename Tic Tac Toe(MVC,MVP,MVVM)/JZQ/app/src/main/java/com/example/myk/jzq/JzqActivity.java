package com.example.myk.jzq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.myk.jzq.JzqActivity.Player.O;
import static com.example.myk.jzq.JzqActivity.Player.X;

public class JzqActivity extends AppCompatActivity {
    public enum Player {X,O}

    public class Cell {
        public Player value;
    }

    private Cell[][] cells = new Cell[3][3];

    private enum GameState {IN_PROGRESS,FINISHED}

    private Player winner;
    private GameState state;
    private Player currentTurn;

    private ViewGroup buttonGrid;
    private View winnerPlayerViewGroup;
    private TextView winnerPlayerLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jzq);
        winnerPlayerLabel = (TextView)findViewById(R.id.winnerPlayerLabel1);
        winnerPlayerViewGroup = findViewById(R.id.winnerPlayerViewGroup);
        buttonGrid = (ViewGroup) findViewById(R.id.buttonGrid);
        restart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                restart();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    public void onCellClicked(View v) {
        Button button = (Button)v;
        String tag = button.getTag().toString();
        int row = Integer.valueOf(tag.substring(0,1));
        int col = Integer.valueOf(tag.substring(1,2));
        Player playerThatMoved = mark(row,col);
        if (playerThatMoved != null) {
            button.setText(playerThatMoved.toString());
            if (getWinner() != null) {
                winnerPlayerLabel.setText(playerThatMoved.toString());
                winnerPlayerViewGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    private Player getWinner() {
        return  winner;
    }

    private Player mark(int row, int col) {
        Player playerThatMoved = null;
        if (isValid(row,col)) {
            cells[row][col].value = currentTurn;
            playerThatMoved = currentTurn;
            if (isWinningMoveByPlayer(currentTurn,row,col)) {
                state = GameState.FINISHED;
                winner = currentTurn;
            } else {
                //换棋子
                flipCurrentTurn();
            }
        }

        return playerThatMoved;
    }

    private void flipCurrentTurn() {
        currentTurn = currentTurn == X ? O : X;
    }

    private boolean isWinningMoveByPlayer(Player currentTurn, int row, int col) {
        return (cells[row][0].value == currentTurn
                && cells[row][1].value == currentTurn
                && cells[row][2].value == currentTurn
                || cells[0][col].value == currentTurn
                && cells[1][col].value == currentTurn
                && cells[2][col].value == currentTurn
                || row == col
                && cells[0][0].value == currentTurn
                && cells[1][1].value == currentTurn
                && cells[2][2].value == currentTurn
                || row + col == 2
                && cells[0][2].value == currentTurn
                && cells[1][1].value == currentTurn
                && cells[2][0].value == currentTurn);
    }

    private boolean isValid(int row, int col) {
        if (state == GameState.FINISHED) {
            return false;
//        } else if (isOutOfBounds(row) || isOutOfBounds(col)) {
//            return false;
        } else if (isCellValueAlreadySet(row,col)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isCellValueAlreadySet(int row, int col) {
        return cells[row][col].value != null;
    }

//    private boolean isOutOfBounds(int row) {
//    }

    private void restart() {
        clearCells();
        winner = null;
        currentTurn = X;
        state = GameState.IN_PROGRESS;
        winnerPlayerViewGroup.setVisibility(View.GONE);
        winnerPlayerLabel.setText("");
        int count = buttonGrid.getChildCount();
        for (int i = 0; i < count;i++)
            ((Button) buttonGrid.getChildAt(i)).setText("");
    }

    private void clearCells() {
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++)
                cells[i][j] = new Cell();
        }
    }
}
