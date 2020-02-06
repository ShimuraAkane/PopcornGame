package jp.ac.shohoku.teamu.popcorngame;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SensorManager graSensorManager;
    private AccelerationGraSensor accelerationGraSensor;

    private Timer timer;
    private MainView mview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mview = (MainView)findViewById(R.id.mainview);

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
                mview.setSensorValue(accelerationGraSensor.getY());
            }
        }, 0, 10);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
