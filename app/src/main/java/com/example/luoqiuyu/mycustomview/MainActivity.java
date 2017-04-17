package com.example.luoqiuyu.mycustomview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show_radar_view).setOnClickListener(this);
        findViewById(R.id.show_search_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show_radar_view:
                Intent intent = new Intent(MainActivity.this,RadarActivity.class);
                startActivity(intent);
                break;
            case R.id.show_search_view:
                Intent intent2 = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent2);
                break;

        }
    }
}
