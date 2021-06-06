package com.example.myk.jzq_mvp.view;

public interface JzqiView {

    void showWinner(String playerDisplayLabel);
    void clearWinnerDisplay();
    void clearButtons();
    void setButtonText(int row,int col,String content);

}
