package com.example.livedatabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LiveDataBus.get().with("MainActivity",Work.class).observe(this, new Observer<Work>() {
            @Override
            public void onChanged(Work work) {
                if (work != null) {
                    Toast.makeText(MainActivity.this, work.getType(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_main_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Work work = new Work("qer","ertew");
                LiveDataBus.get().with("MainActivity",Work.class).postValue(work);
            }
        });

        findViewById(R.id.btn_main_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(MainActivity.this,SecondActivity.class));
            }
        });
    }
}