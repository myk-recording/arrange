package com.example.livedatabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        LiveDataBus.get().with("MainActivity",Work.class).observe(this, new Observer<Work>() {
            @Override
            public void onChanged(Work work) {
                if (work != null) {
                    Toast.makeText(SecondActivity.this, work.getType(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_second_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Work work = new Work("qer","adfad");
                LiveDataBus.get().with("MainActivity",Work.class).postValue(work);
            }
        });
    }
}