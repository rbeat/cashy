package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Assistant extends AppCompatActivity {

    TextView textExample;
    ImageView imgView;
    Button talk, spend, balance;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {





        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale("en_US"));
                }
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant2);
        talk = findViewById(R.id.talk);
        textExample = findViewById(R.id.textExample);
        imgView = findViewById(R.id.imgView);
        spend = findViewById(R.id.spendBt);
        balance = findViewById(R.id.balanceBt);
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test = "Hello";
                tts.speak(test, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        spend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String test = "Sorry, but you're empty. You can work harder next time, to buy those snickers.";
                Toast.makeText(Assistant.this, test, Toast.LENGTH_SHORT).show();
                tts.speak(test, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test = "Your balance is zero. You should consider finding a job.";
                Toast.makeText(Assistant.this, test, Toast.LENGTH_SHORT).show();
                tts.speak(test, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause(){
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }


    public boolean canSpend (double balanceWithOwes, double toSpend){
        if (balanceWithOwes<toSpend){
            return false;
        }
        else{
            return true;
        }
    }
}
