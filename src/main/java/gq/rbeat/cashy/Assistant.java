package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class Assistant extends AppCompatActivity {

    TextView textExample;
    ImageView imgView;
    Button add, spend, balance, bills;
    TextToSpeech tts;
    DatabaseReference mDatabase;
    String name, email;
    User current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
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
        bills = findViewById(R.id.billsBt);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                current.recalculateAvailable();
                mDatabase.child(email).setValue(current);
                name = current.getName();
                String welcomeScreen = "Welcome, " + name + "!";
                spend.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                balance.setVisibility(View.VISIBLE);
                bills.setVisibility(View.VISIBLE);
                textExample.setText(welcomeScreen);
                tts.speak(welcomeScreen + "What you gonna do today?", TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Assistant.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Assistant.this, AddingMoney.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        spend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Assistant.this, Spend.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Assistant.this, BalanceScreen.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Assistant.this, AddBills.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

    }

    public void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }


}
