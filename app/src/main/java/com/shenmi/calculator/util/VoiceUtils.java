package com.shenmi.calculator.util;

import android.content.Context;
import android.database.Observable;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * @author 语音播放Util
 * Created by Mr on 2018/10/25.
 */

public class VoiceUtils {

    private TextToSpeech mTts;

    public VoiceUtils(Context context) {
        mTts = new TextToSpeech(context.getApplicationContext(), mInitListener);
    }

    private final TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(Locale.CHINESE);
                mTts.setPitch(1.0f);
                mTts.setSpeechRate(1.0f);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("VoiceUtils", "Language is not available.");
                }
            } else {
                Log.e("VoiceUtils", "Could not initialize TextToSpeech.");
            }
        }
    };



    /**
     * 关闭TTS
     */
    public void closeVoice() {
        if (null != mTts) {
            mTts.stop();
            mTts.shutdown();
        }
    }
}
