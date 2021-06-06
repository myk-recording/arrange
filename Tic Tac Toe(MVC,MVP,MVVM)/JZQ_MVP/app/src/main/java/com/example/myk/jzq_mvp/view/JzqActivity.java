package com.example.myk.jzq_mvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myk.jzq_mvp.R;
import com.example.myk.jzq_mvp.presenter.JzqPresenter;

public class  JzqActivity extends AppCompatActivity implements JzqiView {

    private ViewGroup buttonGrid;
    private View winnerPlayerView;
    private TextView winnerPlayerTextView;

    private JzqPresenter presenter = new JzqPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jzq);
        winnerPlayerTextView = findViewById(R.id.winnerPlayerLabel1);
        winnerPlayerView = findViewById(R.id.winnerPlayerViewGroup);
        buttonGrid = findViewById(R.id.buttonGrid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                presenter.onResetSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCellClicked(View v) {
        Button button = (Button) v;
        String tag = button.getTag().toString();
        int row = Integer.valueOf(tag.substring(0,1));
        int col = Integer.valueOf(tag.substring(1,2));
        presenter.onButtonSelected(row,col);
    }

    @Override
    public void showWinner(String playerDisplayLabel) {
        winnerPlayerView.setVisibility(View.VISIBLE);
        winnerPlayerTextView.setText(playerDisplayLabel);
    }

    @Override
    public void clearWinnerDisplay() {

    }

    @Override
    public void clearButtons() {

    }

    @Override
    public void setButtonText(int row, int col, String content) {

    }
}
