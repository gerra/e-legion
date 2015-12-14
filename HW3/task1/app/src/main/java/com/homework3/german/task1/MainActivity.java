package com.homework3.german.task1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private EditText firstNumberView;
    private EditText secondNumberView;
    private TextView resultView;
    private BroadcastReceiver receiver;

    private void startService(String operation) {
        Intent intent = new Intent("homework3.task1.service.START_SERVICE");
        intent.putExtra("OPERATION", operation);
        intent.putExtra("OPERAND_1", firstNumberView.getText().toString());
        intent.putExtra("OPERAND_2", secondNumberView.getText().toString());
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstNumberView = (EditText) findViewById(R.id.first_number);
        secondNumberView = (EditText) findViewById(R.id.second_number);
        resultView = (TextView) findViewById(R.id.result);
        findViewById(R.id.op_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService("+");
            }
        });
        findViewById(R.id.op_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService("-");
            }
        });
        findViewById(R.id.op_mul).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService("*");
            }
        });
        findViewById(R.id.op_div).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService("/");
            }
        });
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("MainActivity", "onReceive");

                double result = intent.getDoubleExtra("RESULT", Double.NaN);

                if (result == result) {
                    resultView.setText(String.valueOf(result));
                } else {
                    if (intent.getBooleanExtra("DVB", false)) {
                        resultView.setText(R.string.dvb_error);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.homework3.german.task1.CALC_RESULT"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
