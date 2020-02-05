package jp.ac.shohoku.teamu.popcorngame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.animation.*;
import android.media.AudioManager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public final int FIRST = 1; //タイトル画面
    public final int SECOND = 2; //プレイ画面
    public final int THIRD = 3; //ゲームリザルト
    private MediaPlayer titlePlayer;  // タイトルのBGM用
    private MediaPlayer playPlayer;   // ゲームプレイ中のBGM用
    private MediaPlayer resultPlayer; // リザルト画面のBGM用
    int state; //状態を表す変数
    Bitmap start = BitmapFactory.decodeResource(getResources(), R.drawable.start);
    Bitmap retry = BitmapFactory.decodeResource(getResources(), R.drawable.retry);
    Bitmap title = BitmapFactory.decodeResource(getResources(), R.drawable.start);
    Bitmap popcornCover = BitmapFactory.decodeResource(getResources(), R.drawable.popcover);
    //Bitmap[] number = new Bitmap[10];

    private SurfaceHolder mHolder;
    private long mLvStart, mLvTime;  //レベルの開始とレベルの時間
    ArrayList<PopcornSample> popcorns = new ArrayList<PopcornSample>();
    private int popcornNum;
    private boolean gameFlag;

    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public MainView(Context context, AttributeSet attrs) {
        super (context, attrs);
        init();
    }

    private void init() {  //初期化
        mHolder = getHolder();  //SurfaceHolder取得
        mHolder.addCallback(this);
        setFocusable(true);
        requestFocus();
        state = FIRST;  //はじめは状態 1
        titlePlay();    //タイトルのBGM
        mLvStart = System.currentTimeMillis();
        for(int i=0; i<10; i++){
            popcorns.add(new PopcornSample(this));
        }
        popcornNum = 0;
        gameFlag = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        int i;

        //長方形の内部で

        if (state == FIRST) {  //状態１だったら状態２へ
            if(x>300 && x<900 && y>1150 && y<1250) {
                titleStop();  // タイトルBGMをストップ
                playPlay();   // ゲーム中のBGMをスタート
                state = SECOND;
                mLvStart = System.currentTimeMillis();
            }
        } else if (state == SECOND) {  //状態２だったら
            if(gameFlag == true){
                popcorns.add(new PopcornSample(this));
                popcornNum++;
            }
        } else if(state == THIRD){  //状態３だったら状態1へ
            if(x>300 && x<900 && y>1050 && y<1150) {  //リトライ
                resultStop();  // 一旦止める 止めないとエラーが起こるから
                playPlay();  // またスタートさせる
                popcornNum = 0;
                state = SECOND;
                mLvStart = System.currentTimeMillis();
            }
            if(x>300 && x<900 && y>1200 && y<1300) {  //タイトル画面へ
                resultStop();  // ゲーム中のBGMをストップ
                for(i = 0; i < popcornNum; i++){
                    popcorns.get(i).shokika();
                }
                popcornNum = 0;
                state = FIRST;
                titlePlay(); // タイトルBGMをスタート
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
        mLvTime = System.currentTimeMillis() - mLvStart;
        if (state == SECOND) {
            if(mLvTime >= 3000){
                gameFlag = true;
            }
            if(gameFlag == true){
                for(int i=0; i<popcornNum; i++) {
                    popcorns.get(i).move();
                }
            }
            if(mLvTime >= 12000) {
                gameFlag = false;
            }
            if(mLvTime >= 13500) {
                state = THIRD;
                playStop();
                resultPlay();
                for(int i=0; i<popcornNum; i++){
                    popcorns.get(i).shokika();
                }
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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    // SurfaceViewが終了した時に呼び出される
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        titleStop();  // アプリが終了したら音楽を止める
    }

    private void draw(){
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setTextSize(200);
        if (state == FIRST) { //状態 1 の場合の描画
            canvas.drawBitmap(start, 300, 1150, p);
            Log.v("draw", "スタート画面");
        }
        else if (state == SECOND) { //状態 2 の場合の描画
            canvas.drawARGB(255, 255, 255, 0);

            for(int i=0; i<popcornNum; i++){
                popcorns.get(popcornNum).draw(canvas, p, popcorns.get(i).x, popcorns.get(i).y);
            }

            if(mLvTime <= 1000){
                canvas.drawText("3", 500, 700, p);
            }else if(mLvTime <= 2000){
                canvas.drawText("2", 500, 700, p);
            }else if(mLvTime <= 3000){
                canvas.drawText("1", 500, 700, p);
            }else if(mLvTime <= 4000){
                canvas.drawText("GO", 450, 700, p);
            }
            if(gameFlag == false && mLvTime >= 12000){
                canvas.drawText("FINISH", 450, 700, p);
            }

            canvas.drawBitmap(popcornCover, 100, 1250, p);
            Log.v("draw", "状態２になった");
        }
        else if(state == THIRD) {
            canvas.drawARGB(255, 255, 255, 255);
            p.setTextSize(150);
            canvas.drawText(String.valueOf(popcornNum), 200, 200, p);
            canvas.drawBitmap(retry, 300, 1050, p);
            canvas.drawBitmap(start, 300, 1200, p);
            Log.v("draw", "状態3になった");
        }
        else { //それ以外の場合は，Log にエラーを吐き出す
            Log.d("error", "never come here");
        }
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void countDown(){

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



}
