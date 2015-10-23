package com.homework3.german.task3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 30.09.15.
 */
public class ActivityB extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ((TextView) findViewById(R.id.current_activity)).setText("B");
        findViewById(R.id.go_to_b).setVisibility(View.INVISIBLE);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityB.this, ActivityC.class);
                startActivity(intent);
            }
        });
    }
}
