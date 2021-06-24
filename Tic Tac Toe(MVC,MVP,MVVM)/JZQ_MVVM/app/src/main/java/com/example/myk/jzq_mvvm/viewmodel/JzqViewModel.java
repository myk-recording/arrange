package com.example.myk.jzq_mvvm.viewmodel;

import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;

import com.example.myk.jzq_mvvm.model.Board;
import com.example.myk.jzq_mvvm.model.Player;

public class JzqViewModel {

    private Board board;
    public final ObservableArrayMap<String,String> cells = new ObservableArrayMap<>();
    public final ObservableField<String> winner = new ObservableField<>();

    public JzqViewModel() {
        board = new Board();
    }

    public void onResetSelected() {
        board.restart();
        winner.set(null);
        cells.clear();
    }

    public void onClickedCellAt(int row,int col) {
        Player player = board.mark(row,col);
        if (player != null) {
            cells.put("" + row + col,player.toString());
            winner.set(board.getWinner() == null ? null : board.getWinner().toString());
        }
    }

}
