package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Locale;

public class BalanceScreen extends AppCompatActivity {

    TextView tv;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_screen);
        Intent intent = getIntent();
        imgView = findViewById(R.id.imgView);
        email = intent.getStringExtra("email");
        tv = findViewById(R.id.textBalance);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale("en_US"));
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                continueBalance();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BalanceScreen.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void anim() {
        ImageView image = findViewById(R.id.imgView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    public void continueBalance() {
        anim();
        tts.speak("Here's how broke we are.", TextToSpeech.QUEUE_FLUSH, null);
        current.recalculateAvailable();
        Double balance = current.getPersonalBalance();

        Double credit = current.getCreditBalance();

        Double available = current.getAvailable();

        if (balance <= 35) {
            imgView.setImageResource(R.drawable.confused);
            tts.speak("Be careful! You're low on balance.", TextToSpeech.QUEUE_ADD, null);
        }

        if (available <= 35) {
            imgView.setImageResource(R.drawable.angry);
            tts.speak("Man, stop spending your money. You are too low on balance and credit.", TextToSpeech.QUEUE_ADD, null);
        }

        if (available > 35 && balance > 35) {
            imgView.setImageResource(R.drawable.angry);
            tts.speak("No worries. Everything's OK.", TextToSpeech.QUEUE_ADD, null);
        }
        tv.setText("Balance: " + new DecimalFormat("##.##").format(balance) + "\nCredit: " + new DecimalFormat("##.##").format(credit) + "\nAvailable: " + new DecimalFormat("##.##").format(available));

    }


}
