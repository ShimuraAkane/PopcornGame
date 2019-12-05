package jp.ac.shohoku.teamu.popcorngame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class PopcornSample extends Activity {

    public PopcornSample(){

    }

    public void draw(Canvas canvas, Paint paint){
        Bitmap popcorn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tekitou), 100, 100, true);
        canvas.drawBitmap(popcorn, 100, 100, paint);
    }


}
