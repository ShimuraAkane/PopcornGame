package jp.ac.shohoku.teamu.popcorngame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public final int FIRST = 1; //タイトル画面
    public final int SECOND = 2; //プレイ画面
    public final int THIRD = 3; //ゲームリザルト
    int state; //状態を表す変数
    private long gameStart, gameTime;
    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.start);

    private SurfaceHolder mHolder;
    private int mGameState;  //ゲームの状態を表す変数
    private long mLvStart, mLvTime;  //レベルの開始とレベルの時間

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
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Paint p = new Paint();
//        if (state == FIRST) { //状態 1 の場合の描画
//            canvas.drawBitmap(bmp, 300, 1150, p);
//        } else if (state == SECOND) { //状態 2 の場合の描画
//            canvas.drawARGB(255, 255, 255, 0);
//        } else if(state == THIRD) {
//            canvas.drawARGB(255, 255, 255, 255);
//        }
//        else { //それ以外の場合は，Log にエラーを吐き出す
//            Log.d("error", "never come here");
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();

        //長方形の内部で

        if (state == FIRST) {  //状態１だったら状態２へ
            if(x>300 && x<900 && y>1150 && y<1250) {
                state = SECOND;
                mLvStart = System.currentTimeMillis();
            }
        } else if (state == SECOND) {  //状態２だったら押しても意味ない

        } else if(state == THIRD){  //状態３だったら状態1へ
            state = FIRST;
        }
        else {  //それ以外だったらエラーを吐き出す
            Log.d("error", "never come here");
        }

        invalidate();  //再描画
        return super.onTouchEvent(event);
    }

    private void start(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.MILLISECONDS); //スタート画面→黄色い画面→白い画面
    }

    /*
     * 実行可能メソッド．このクラスの中では定期実行される
     *
     * @see java.lang.Runnable#run()
     */
    public void run(){
        mLvTime = System.currentTimeMillis() - mLvStart;

        if(state == SECOND){
            if(mLvTime >= 3000){
                state = THIRD;
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
        if (state == FIRST) { //状態 1 の場合の描画
            canvas.drawBitmap(bmp, 300, 1150, p);
            Log.v("draw", "スタート画面");
        } else if (state == SECOND) { //状態 2 の場合の描画
            canvas.drawARGB(255, 255, 255, 0);
            Log.v("draw", "状態２になった");
        } else if(state == THIRD) {
            canvas.drawARGB(255, 255, 255, 255);
            Log.v("draw", "状態3になった");
        }
        else { //それ以外の場合は，Log にエラーを吐き出す
            Log.d("error", "never come here");
        }
        mHolder.unlockCanvasAndPost(canvas);
    }
}
