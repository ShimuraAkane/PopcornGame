package jp.ac.shohoku.teamu.popcorngame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import java.util.Random;

public class PopcornSample {
    Bitmap popcorn;
    int randomValue;
    int x, y;  //発射点
    int speed;
    boolean top;
    int type;
    int xType;
    boolean stop;

    public PopcornSample(SurfaceView sView){
        popcorn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(sView.getResources(), R.drawable.dhiguda), 100, 100, true);
        x=550;  //発射点x
        y=400;  //発射点y
        speed = 20;
        top = false;
        Random random = new Random();
        type = random.nextInt(6); //0-5
        xType = type * 4 - 10;
        stop = false;
        if(!popcorn.isRecycled()){
            popcorn.recycle();
        }
        popcorn = null;
        popcorn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(sView.getResources(), R.drawable.dhiguda), 100, 100, true);
    }

    public void shokika(){
        if(!popcorn.isRecycled()){
            popcorn.recycle();
        }
        if(popcorn != null){
            popcorn = null;
        }
    }

    public void draw(Canvas canvas, Paint paint, int x, int y){
        if(stop == false){
            canvas.drawBitmap(popcorn, x, y, paint);
        }
    }

    public void move(){

        if(stop == false){
            this.x = this.x + xType;

            if(this.y < 100){
                top = true;
            }
            if (top == false){
                this.y = this.y - this.speed;
                speed = speed - 2;
            } else{
                this.y = this.y + this.speed;
                speed = speed + 2;
            }
            if(this.y > 1300){
                stop = true;
                this.shokika();
            }
        }
    }
}
