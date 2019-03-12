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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Assistant extends AppCompatActivity {

    TextView textExample;
    ImageView imgView;
    Button add, spend, balance;
    TextToSpeech tts;
    DatabaseReference mDatabase;
    String name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email =  intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference();



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
        add = findViewById(R.id.add);
        textExample = findViewById(R.id.textExample);
        imgView = findViewById(R.id.imgView);
        spend = findViewById(R.id.spendBt);
        balance = findViewById(R.id.balanceBt);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User current = dataSnapshot.child("Users").child(email).getValue(User.class);
                name = current.getName();
                textExample.setText("Welcome, " + name + "!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Assistant.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
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
