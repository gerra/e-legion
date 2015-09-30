package com.homework3.german.task3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 30.09.15.
 */
public class ActivityD extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ((TextView) findViewById(R.id.current_activity)).setText("D");
        findViewById(R.id.next).setVisibility(View.INVISIBLE);
        findViewById(R.id.go_to_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityD.this, ActivityB.class);
                startActivity(intent);
            }
        });
    }
}