package jp.ac.shohoku.teamu.popcorngame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;



public class PopcornSample {
    Bitmap popcorn;
    int randomValue;
    int x, y;  //発射点
    int speed;
    boolean top;
    public PopcornSample(SurfaceView sView){
        popcorn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(sView.getResources(), R.drawable.tekitou), 100, 100, true);
        x=600;  //発射点x
        y=500;  //発射点y
        speed = 15;
        top = false;
    }

    public void shokika(){
        x=600;  //発射点x
        y=500;  //発射点y
        speed = 10;
        top = false;
    }

    public void draw(Canvas canvas, Paint paint, int x, int y){
        canvas.drawBitmap(popcorn, x, y, paint);
    }

    public void move(){
        //Random random = new Random();
        //randomValue = random.nextInt(1);
        this.x = this.x + 10;
        if(this.y < 350){
            top = true;
        }
        if(top == false){
            this.y = this.y - this.speed;
            speed = speed - 2;
        } else{
            this.y = this.y + this.speed;
            speed = speed + 2;
        }

    }

}
