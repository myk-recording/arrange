package com.example.myk.jzq_mvvm;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myk.jzq_mvvm.databinding.ActivityJzqBinding;
import com.example.myk.jzq_mvvm.viewmodel.JzqViewModel;

public class JzqActivity extends AppCompatActivity {

    JzqViewModel viewModel = new JzqViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJzqBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_jzq);
        binding.setViewModel(viewModel);
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
                viewModel.onResetSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
