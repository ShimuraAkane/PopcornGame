package jp.ac.shohoku.teamu.popcorngame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import android.media.MediaPlayer;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

public class MainView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public final int FIRST = 1; //タイトル画面
    public final int SECOND = 2; //プレイ画面
    public final int THIRD = 3; //ゲームリザルト
    private MediaPlayer titlePlayer;  // タイトルのBGM用
    private MediaPlayer playPlayer;   // ゲームプレイ中のBGM用
    private MediaPlayer resultPlayer; // リザルト画面のBGM用
    int state; //状態を表す変数

    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    Bitmap titleLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
    Bitmap startButton = BitmapFactory.decodeResource(getResources(), R.drawable.start);

    Bitmap popcornMachine = BitmapFactory.decodeResource(getResources(), R.drawable.machine);
    Bitmap popcornMorimori0 = BitmapFactory.decodeResource(getResources(), R.drawable.pop0);
    Bitmap popcornMorimori1 = BitmapFactory.decodeResource(getResources(), R.drawable.pop1);
    Bitmap popcornMorimori2 = BitmapFactory.decodeResource(getResources(), R.drawable.pop2);
    Bitmap popcornMorimori3 = BitmapFactory.decodeResource(getResources(), R.drawable.pop3);
    Bitmap popcornMorimori4 = BitmapFactory.decodeResource(getResources(), R.drawable.pop4);
    Bitmap popcornMorimori5 = BitmapFactory.decodeResource(getResources(), R.drawable.pop5);
    Bitmap go = BitmapFactory.decodeResource(getResources(), R.drawable.go);
    Bitmap finish = BitmapFactory.decodeResource(getResources(), R.drawable.finish);

    Bitmap score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
    Bitmap rank1 = BitmapFactory.decodeResource(getResources(), R.drawable.rank1);
    Bitmap rank2 = BitmapFactory.decodeResource(getResources(), R.drawable.rank2);
    Bitmap rank3 = BitmapFactory.decodeResource(getResources(), R.drawable.rank3);
    Bitmap retryButton = BitmapFactory.decodeResource(getResources(), R.drawable.retry);
    Bitmap titleButton = BitmapFactory.decodeResource(getResources(), R.drawable.title);

    /* 数字 */
    Bitmap[] number = new Bitmap[4];

    private SurfaceHolder mHolder;
    private long gameStart, gameTime;  //レベルの開始とレベルの時間
    ArrayList<PopcornSample> popcorns = new ArrayList<PopcornSample>();
    private int popcornNum;
    private boolean gameFlag;
    private SensorManager graSensorManager;
    private AccelerationGraSensor accelerationGraSensor;
    private float sensorNum;
    private int scoreNum;
    private Canvas canvas;
    private Paint p = new Paint();
    private int[] ranking = new int[3];
    private boolean rankingFlag;
    private int rankIn;

    //音楽用のフィールドとSoundPoolのフィールド
    int hue; // ホイッスル
    int hop; // ポップコーンがはじける音
    SoundPool soundPool;

    //音楽再生用のメソッド
    public void pipi(){soundPool.play(hue,1f , 1f, 0, 0, 1f);}    // GOとFINISHのホイッスルを流す
    public void pophop(){soundPool.play(hop,1f , 1f, 0, 0, 1f);}  // ポップコーンをポコポコ鳴らす

    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public MainView(Context context, AttributeSet attrs) {
        super (context, attrs);
        init();
        Hue();
    }

    private void init() {  //初期化
        mHolder = getHolder();  //SurfaceHolder取得
        mHolder.addCallback(this);
        setFocusable(true);
        requestFocus();
        number[1] = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        number[2] = BitmapFactory.decodeResource(getResources(), R.drawable.two);
        number[3] = BitmapFactory.decodeResource(getResources(), R.drawable.three);
        state = FIRST;  //はじめは状態 1
        titlePlay();    //タイトルのBGM
        gameStart = System.currentTimeMillis();
        for(int i=0; i<10; i++){
            popcorns.add(new PopcornSample(this));
        }
        popcornNum = 0;
        scoreNum = 0;
        gameFlag = false;
        for(int i = 0; i < 3; i++){
            ranking[i] = 0;
        }
        rankingFlag = false;
        rankIn = -1;
    }

    public void setSensorValue(float sensorValue){
        sensorNum = sensorValue;
    }

    public void setFont(AppCompatActivity act){
        p.setTypeface(Typeface.createFromAsset(act.getAssets(), "Kikakana-21-Regular.otf"));
    }
    public void shokika(){
        popcorns.clear();
        for(int i=0; i<10; i++){
            popcorns.add(new PopcornSample(this));
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        int i;

        //長方形の内部で

        if (state == FIRST) {  //状態１だったら状態２へ
            if(x>300 && x<900 && y>1150 && y<1300) {
                titleStop();  // タイトルBGMをストップ
                playPlay();   // ゲーム中のBGMをスタート
                state = SECOND;
                gameStart = System.currentTimeMillis();
            }
        } else if (state == SECOND) {  //状態２だったら

        } else if(state == THIRD){  //状態３だったら状態1へ
            this.shokika();
            if(scoreNum == popcornNum){
                if(x>300 && x<900 && y>1050 && y<1200) {  //リトライ
                    resultStop();  // 一旦止める 止めないとエラーが起こるから
                    playPlay();  // またスタートさせる
                    popcornNum = 0;
                    scoreNum = 0;
                    state = SECOND;
                    rankingFlag = false;
                    rankIn = -1;
                    gameStart = System.currentTimeMillis();
                }
                if(x>300 && x<900 && y>1300 && y<1450) {  //タイトル画面へ
                    resultStop();  // ゲーム中のBGMをストップ
                    popcornNum = 0;
                    scoreNum = 0;
                    state = FIRST;
                    rankingFlag = false;
                    rankIn = -1;
                    titlePlay(); // タイトルBGMをスタート
                }
            }
        }
        else {  //それ以外だったらエラーを吐き出す
            Log.d("error", "never come here");
        }
        invalidate();  //再描画
        return super.onTouchEvent(event);
    }
    private void start(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.MILLISECONDS);  //スタート画面→黄色い画面→白い画面
    }
    /*
     * 実行可能メソッド．このクラスの中では定期実行される
     * @see java.lang.Runnable#run()
     */
    public void run(){
        gameTime = System.currentTimeMillis() - gameStart;
        if (state == SECOND) {
            if(gameTime >= 3000){
                if(gameTime >= 18000) {
                    gameFlag = false;
                }else{
                    gameFlag = true;
                }
            }
            if(gameFlag == true){
                if(sensorNum >= 5){
                    popcorns.add(new PopcornSample(this));
                    pophop();
                    popcornNum++;
                    if(sensorNum >= 20){
                        popcorns.add(new PopcornSample(this));
                        pophop();
                        popcornNum++;
                        if(sensorNum >= 30){
                            popcorns.add(new PopcornSample(this));
                            pophop();
                            popcornNum++;
                        }
                    }
                }
                if(sensorNum <= -5){
                    popcorns.add(new PopcornSample(this));
                    pophop();
                    popcornNum++;
                    if(sensorNum <= -20){
                        popcorns.add(new PopcornSample(this));
                        pophop();
                        popcornNum++;
                        if(sensorNum <= -30){
                            popcorns.add(new PopcornSample(this));
                            pophop();
                            popcornNum++;
                        }
                    }
                }
            }
            for(int i=0; i<popcornNum; i++) {
                popcorns.get(i).move();
            }
            if(gameTime >= 21000) {
                state = THIRD;
                playStop();
                resultPlay();
            }
        }
        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        draw();
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

    // SurfaceViewが終了した時に呼び出される
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        titleStop();  // アプリが終了したら音楽を止める
    }

    private void draw(){
        canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        p.setTextSize(180);
        canvas.drawBitmap(background, 0, 0, p);
        if (state == FIRST) { //状態 1 の場合の描画
            canvas.drawBitmap(titleLogo, 100, 0, p);
            canvas.drawBitmap(startButton, 300, 1150, p);
            Log.v("draw", "スタート画面");
        }
        else if (state == SECOND) { //状態 2 の場合の描画
            canvas.drawBitmap(popcornMachine, -12, 0, p);
            for(int i=0; i<popcornNum; i++){
                popcorns.get(popcornNum).draw(canvas, p, popcorns.get(i).x, popcorns.get(i).y);
            }
            if(gameTime <= 1000){
                canvas.drawBitmap(number[3], 450, 500, p);
            }else if(gameTime <= 2000){
                canvas.drawBitmap(number[2], 450, 500, p);
            }else if(gameTime <= 3000){
                canvas.drawBitmap(number[1], 450, 500, p);
                pipi();
            }else if(gameTime <= 4000){
                canvas.drawBitmap(go, 450, 500, p);
            }
            if(popcornNum < 100){
                canvas.drawBitmap(popcornMorimori0, -12, 0, p);
            }else if(popcornNum < 200){
                canvas.drawBitmap(popcornMorimori1, -12, 0, p);
            }else if(popcornNum < 300){
                canvas.drawBitmap(popcornMorimori2, -12, 0, p);
            }else if(popcornNum < 400){
                canvas.drawBitmap(popcornMorimori3, -12, 0, p);
            }else if(popcornNum < 500){
                canvas.drawBitmap(popcornMorimori4, -12, 0, p);
            }else{
                canvas.drawBitmap(popcornMorimori5, -12, 0, p);
            }
            if(gameTime >= 15000 && gameTime <=18000){
                pipi();
            }
            if(gameFlag == false && gameTime >= 18000 && gameTime <= 21000){
                canvas.drawBitmap(finish, 450, 500, p);
            }
            Log.v("draw", "状態２になった");
        }
        else if(state == THIRD) {
            canvas.drawBitmap(score, 450, 0, p);
            p.setTextSize(200);
            if(scoreNum < 10){
                canvas.drawText(String.valueOf(scoreNum), 525, 455, p);
            }else if(scoreNum < 100){
                canvas.drawText(String.valueOf(scoreNum), 450, 455, p);
            }else{
                canvas.drawText(String.valueOf(scoreNum), 375, 455, p);
            }
            if(scoreNum < popcornNum){
                scoreNum++;
                if(scoreNum < popcornNum){
                    scoreNum++;
                }
            }
            Log.v("draw", "ranking前");
            Log.v("draw", "popscore:" + popcornNum);
            if(rankingFlag == false){
                if(popcornNum > ranking[0]){
                    ranking[2] = ranking[1];
                    ranking[1] = ranking[0];
                    ranking[0] = popcornNum;
                    rankIn = 0;
                }else if(popcornNum > ranking[1]){
                    ranking[2] = ranking[1];
                    ranking[1] = popcornNum;
                    rankIn = 1;
                }else if(popcornNum > ranking[2]){
                    ranking[2] = popcornNum;
                    rankIn = 2;
                }
                Log.v("draw", "ranking終了");
                rankingFlag = true;
            }

            if(scoreNum == popcornNum){
                p.setTextSize(75);
                for(int i = 0; i < 3; i++){
                    canvas.drawText(String.valueOf(i+1), 410, 706 + 120 * i, p);
                    if(ranking[i] <= 0){
                        canvas.drawText("-", 722, 700 + 120 * i, p);
                    }else if(ranking[i] < 10){
                        canvas.drawText(String.valueOf(ranking[i]), 722, 700 + 120 * i, p);
                    }else if(ranking[i] < 100){
                        canvas.drawText(String.valueOf(ranking[i]), 666, 700 + 120 * i, p);
                    }else{
                        canvas.drawText(String.valueOf(ranking[i]), 610, 700 + 120 * i, p);
                    }
                }
                p.setTextSize(40);
                if(rankIn == 0){
                    canvas.drawBitmap(rank1, 250, 606, p);
                    canvas.drawText("RANK IN!", 850, 685, p);
                }else if(rankIn == 1){
                    canvas.drawBitmap(rank2, 250, 726, p);
                    canvas.drawText("RANK IN!", 850, 805, p);
                }else if(rankIn == 2){
                    canvas.drawBitmap(rank3, 250, 846, p);
                    canvas.drawText("RANK IN!", 850, 925, p);
                }else{}
                canvas.drawBitmap(retryButton, 300, 1050, p);
                canvas.drawBitmap(titleButton, 300, 1300, p);
            }
            Log.v("draw", "状態3になった");
        }
        else { //それ以外の場合は，Log にエラーを吐き出す
            Log.d("error", "never come here");
        }
        mHolder.unlockCanvasAndPost(canvas);
    }

    // ここからBGM
    private boolean titleSetup(){
        boolean fileCheck = false;
        // 繰り返し再生する場合
        if (titlePlayer != null) {
            titlePlayer.stop();
            titlePlayer.reset();
            // リソースの解放
            titlePlayer.release();
        }
        // rawにファイルがある場合
        titlePlayer = MediaPlayer.create(getContext(), R.raw.title);
        // 無限ループ
        titlePlayer.setLooping(true);
        fileCheck = true;
        return fileCheck;
    }

    private void titlePlay() {

        if (titlePlayer == null) {
            // audio ファイルを読出し
            if (titleSetup()){
                //Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            // 繰り返し再生する場合
            titlePlayer.stop();
            titlePlayer.reset();
            // リソースの解放
            titlePlayer.release();
        }

        // 再生する
        titlePlayer.start();

        // 終了を検知するリスナー
        titlePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                //audioStop();
            }
        });
    }

    private void titleStop() {
        // 再生終了
        titlePlayer.stop();
        // リセット
        titlePlayer.reset();
        // リソースの解放
        titlePlayer.release();

        titlePlayer = null;
    }

    private boolean playSetup(){
        boolean fileCheck = false;

        // 繰り返し再生する場合
        if (playPlayer != null) {
            playPlayer.stop();
            playPlayer.reset();
            // リソースの解放
            playPlayer.release();
        }

        // rawにファイルがある場合
        playPlayer = MediaPlayer.create(getContext(), R.raw.play);
        // 無限ループ
        playPlayer.setLooping(true);
        fileCheck = true;

        return fileCheck;

    }

    private void playPlay() {

        if (playPlayer == null) {
            // audio ファイルを読出し
            if (playSetup()){
                //Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            // 繰り返し再生する場合
            playPlayer.stop();
            playPlayer.reset();
            // リソースの解放
            playPlayer.release();
        }

        // 再生する
        playPlayer.start();

        // 終了を検知するリスナー
        playPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                //audioStop();
            }
        });
    }

    private void playStop() {
        // 再生終了
        playPlayer.stop();
        // リセット
        playPlayer.reset();
        // リソースの解放
        playPlayer.release();

        playPlayer = null;
    }

    private boolean resultSetup(){
        boolean fileCheck = false;

        // 繰り返し再生する場合
        if (resultPlayer != null) {
            resultPlayer.stop();
            resultPlayer.reset();
            // リソースの解放
            resultPlayer.release();
        }

        // rawにファイルがある場合
        resultPlayer = MediaPlayer.create(getContext(), R.raw.result);
        // 無限ループ
        resultPlayer.setLooping(true);
        fileCheck = true;

        return fileCheck;

    }

    private void resultPlay() {

        if (resultPlayer == null) {
            // audio ファイルを読出し
            if (resultSetup()){
                //Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            // 繰り返し再生する場合
            resultPlayer.stop();
            resultPlayer.reset();
            // リソースの解放
            resultPlayer.release();
        }

        // 再生する
        resultPlayer.start();

        // 終了を検知するリスナー
        resultPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                //audioStop();
            }
        });
    }

    private void resultStop() {
        // 再生終了
        resultPlayer.stop();
        // リセット
        resultPlayer.reset();
        // リソースの解放
        resultPlayer.release();

        resultPlayer = null;
    }

    // 効果音の初期化
    private void Hue(){
        //soundPoolの初期化
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //1個目のパラメーターはリソースの数に合わせる
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    //パラメーターはリソースの数に合わせる
                    .setMaxStreams(2)
                    .build();
        }
        //音楽の読み込み
        hue = soundPool.load(getContext(), R.raw.hue, 1);
        hop = soundPool.load(getContext(),R.raw.pop,1);
    }


}
