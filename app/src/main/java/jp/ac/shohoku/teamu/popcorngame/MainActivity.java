package jp.ac.shohoku.teamu.popcorngame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SensorManager graSensorManager;
    private AccelerationGraSensor accelerationGraSensor;

    private static final int ACCELERATION_INTERVAL_PERIOD = 1000;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 音量調整を端末のボタンに任せる
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //加速度を取れる状態に設定
        graSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerationGraSensor = new AccelerationGraSensor(graSensorManager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 加速度のログを出力
                Log.d("accelerX", String.valueOf(accelerationGraSensor.getX()));
                Log.d("accelerY", String.valueOf(accelerationGraSensor.getY()));
                Log.d("accelerZ", String.valueOf(accelerationGraSensor.getZ()));
            }
        }, 0, ACCELERATION_INTERVAL_PERIOD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

}
