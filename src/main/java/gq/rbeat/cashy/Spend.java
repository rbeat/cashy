package gq.rbeat.cashy;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.Locale;


public class Spend extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    EditText sumItem, nameItem;
    Button spendActionBt, confirm;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;
    TextView spendText1, spendText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Spend.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        spendActionBt = findViewById(R.id.spendActionBt);
        confirm = findViewById(R.id.confirm);
        sumItem = findViewById(R.id.sumItem);
        nameItem = findViewById(R.id.nameItem);
        spendActionBt.setOnClickListener(this);
        confirm.setOnClickListener(this);
        spendText1 = findViewById(R.id.spendText1);
        spendText2 = findViewById(R.id.spendText2);
        imgView = findViewById(R.id.imgView);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale("en_US"));
                }
            }
        });
        tts.speak("Tell me, how much you want to spend? Spend on what?", TextToSpeech.QUEUE_FLUSH, null);
    }

    public void spend(Double sum, String text) {
        current.makePayment(text, sum);
        mDatabase.child("Users").child(email).setValue(current);
        Intent intent = new Intent(Spend.this, Assistant.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == confirm) {
            Toast.makeText(this, "OK, you can spend them. Adding them to DB...", Toast.LENGTH_SHORT).show();
            tts.speak("OK, you can spend them. Adding them to database...", TextToSpeech.QUEUE_FLUSH, null);
            spend(Double.parseDouble(sumItem.getText().toString()), nameItem.getText().toString());
        }
        if (v == spendActionBt) {
            if (current.getAvailable() > Double.parseDouble(sumItem.getText().toString())) {
                if (current.getPersonalBalance() > Double.parseDouble(sumItem.getText().toString())) {
                    Toast.makeText(this, "OK, you can spend them. Adding them to DB...", Toast.LENGTH_SHORT).show();
                    tts.speak("OK, you can spend them. Adding them to database...", TextToSpeech.QUEUE_FLUSH, null);
                    spend(Double.parseDouble(sumItem.getText().toString()), nameItem.getText().toString());
                } else {

                    spendText1.setText("Your out of money, \nbut you do have credit. Wanna proceed?");
                    tts.speak("Your out of money, but you do have credit. Wanna proceed?", TextToSpeech.QUEUE_FLUSH, null);
                    imgView.setImageResource(R.drawable.confused);
                    spendText2.setVisibility(View.GONE);
                    spendActionBt.setVisibility(View.GONE);
                    sumItem.setVisibility(View.GONE);
                    nameItem.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Sorry, you don't have enough money to spend. You have bills to pay.", Toast.LENGTH_SHORT).show();
                tts.speak("Sorry, you don't have enough money to spend. You have bills to pay.", TextToSpeech.QUEUE_FLUSH, null);
                imgView.setImageResource(R.drawable.angry);
            }
        }
    }


}
