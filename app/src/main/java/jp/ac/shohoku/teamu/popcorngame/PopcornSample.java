package jp.ac.shohoku.teamu.popcorngame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class PopcornSample extends Activity {
    Bitmap popcorn;
    public PopcornSample(){
        popcorn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tekitou), 100, 100, true);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(popcorn, 100, 100, paint);
    }


}
