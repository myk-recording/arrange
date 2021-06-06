package com.example.myk.jzq_mvp.presenter;

import com.example.myk.jzq_mvp.model.Board;
import com.example.myk.jzq_mvp.model.Player;
import com.example.myk.jzq_mvp.view.JzqiView;

public class JzqPresenter {

    private JzqiView view;
    private Board board;

    public JzqPresenter(JzqiView view) {
        this.view = view;
        this.board = new Board();
    }

    public void onButtonSelected(int row,int col) {
        Player player = board.mark(row,col);
        if (player != null) {
            view.setButtonText(row,col,player.toString());
            if (board.getWinner() != null) {
                view.showWinner(player.toString());
            }
        }
    }

    public void onResetSelected() {
        board.restart();
        view.clearWinnerDisplay();
        view.clearButtons();
    }

}
