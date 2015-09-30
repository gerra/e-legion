package com.homework3.german.task1service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CalculatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("CalcService", "OnStartCommand");
	if (intent == null) {
		return super.onStartCommand(intent, flags, startId);
	}
        String operand1AsString = intent.getStringExtra("OPERAND_1");
        String operand2AsString = intent.getStringExtra("OPERAND_2");
        String operation = intent.getStringExtra("OPERATION");
        if (operand1AsString != null && operand2AsString != null && operation != null) {
            try {
                double operand1 = Double.parseDouble(operand1AsString);
                double operand2 = Double.parseDouble(operand2AsString);
                double result = Double.NaN;
                Intent intentToSend = new Intent("com.homework3.german.task1.CALC_RESULT");
                if (operation.equals("+")) {
                    result = operand1 + operand2;
                } else if (operation.equals("-")) {
                    result = operand1 - operand2;
                } else if (operation.equals("*")) {
                    result = operand1 * operand2;
                } else if (operation.equals("/")) {
                    if (operand2 != 0.0) {
                        result = operand1 / operand2;
                    } else {
                        intentToSend.putExtra("DVB", true);
                    }
                } else {
                    throw new RuntimeException("Unknown operation");
                }
                if (result == result) { // check for NaN
                    intentToSend.putExtra("RESULT", result);
                }
                sendBroadcast(intentToSend);
            } catch (Exception e) {
                // ignore
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
