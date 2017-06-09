package com.justbeatit.smartguide.context;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import com.justbeatit.smartguide.R;

import java.util.Locale;

/**
 * Created by Dominik.Czerwinski on 2017-06-09.
 */

public class MessangerImpl implements Messanger {

    private final Context context;
    private final Activity activity;
    private DisablitityType mode;
    private TextToSpeech textToSpeach;
    private TextView textToDisplay;

    public MessangerImpl(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mode = DisablitityType.BLIND;
        switch (mode) {
            case BLIND:
                textToSpeach = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            textToSpeach.setLanguage(new Locale("pl", "PL"));
                        }
                    }
                });
                break;
            case DEAF:
                break;
        }
    }

    @Override
    public void sendMessage(String message) {
        switch(mode) {
            case BLIND:
                textToSpeach.speak(message, TextToSpeech.QUEUE_ADD, null);
            case DEAF:
                Toast textToDisplay = Toast.makeText(context, message, Toast.LENGTH_LONG);
                textToDisplay.show();
                break;
        }
    }

    @Override
    public void setMode(DisablitityType mode) {
        this.mode = mode;
    }

    public enum DisablitityType {
        BLIND, DEAF
    }

}
