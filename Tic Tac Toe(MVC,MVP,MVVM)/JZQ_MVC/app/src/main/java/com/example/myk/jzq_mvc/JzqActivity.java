package com.example.myk.jzq_mvc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myk.jzq_mvc.model.Board;

public class JzqActivity extends AppCompatActivity {

    private Board board;

    private ViewGroup buttonGrid;
    private View winnerPlayerView;
    private TextView winnerPlayerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jzq);
        winnerPlayerTextView = findViewById(R.id.winnerPlayerLabel1);
        winnerPlayerView = findViewById(R.id.winnerPlayerViewGroup);
        buttonGrid = findViewById(R.id.buttonGrid);
        board = new Board();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;    //  返回true才能显示菜单。如果返回false，则不会显示。
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                board.restart();
                resetView();
                return true;    //  返回false以允许正常的菜单处理继续进行，返回true以在此处使用它。
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void resetView() {
    }
}
