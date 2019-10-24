package jp.ac.shohoku.teamu.popcorngame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_view);
        Button button1 = (Button) findViewById(R.id.startButton);
        button1.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if(count == 1){
                    LinearLayout layout = (LinearLayout) findViewById(R.id.titlelayout);
                    layout.removeAllViews();
                    getLayoutInflater().inflate(R.layout.play_view, layout);
                    count = 2;
                }else if(count == 2){
                    LinearLayout layout = (LinearLayout) findViewById(R.id.playlayout);
                    layout.removeAllViews();
                    getLayoutInflater().inflate(R.layout.title_view, layout);
                    count = 1;
                }
            }
        });
    }
}
