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

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public final int FIRST = 1; //タイトル画面
    public final int SECOND = 2; //プレイ画面
    public final int THIRD = 3; //ゲームリザルト
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
                popcornNum = 0;
                state = SECOND;
                mLvStart = System.currentTimeMillis();
            }
            if(x>300 && x<900 && y>1200 && y<1300) {  //タイトル画面へ
                for(i = 0; i < popcornNum; i++){
                    popcorns.get(i).shokika();
                }
                popcornNum = 0;
                state = FIRST;
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

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

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
}
