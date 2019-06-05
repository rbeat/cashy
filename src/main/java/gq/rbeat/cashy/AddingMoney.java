package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Locale;

public class AddingMoney extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;

    TextView addSum, creditSum;
    Button add;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;
    String cred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                cred = current.getCreditBalance() + "";
                init();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddingMoney.this, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
            }
        });
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale(getString(R.string.tts_lang)));
                }
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_money);
        add = findViewById(R.id.AddActionBt);
        imgView = findViewById(R.id.imgView);
        addSum = findViewById(R.id.sumAdd);
        creditSum = findViewById(R.id.creditAdd);


        add.setOnClickListener(this);


    }


    public void addCash(Double sum, Double credit) {
        Double toPut = current.getPersonalBalance() + sum;
        current.setPersonalBalance(toPut);
        current.setCreditBalance(credit);

        mDatabase.child(email).setValue(current);
        Toast.makeText(this, getString(R.string.adding_to_db_toast), Toast.LENGTH_SHORT).show();
        tts.speak(getString(R.string.adding_to_db_tts), TextToSpeech.QUEUE_FLUSH, null);
        Intent intent = new Intent(AddingMoney.this, Assistant.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void init() {
        anim();
        creditSum.setHint(getString(R.string.current_credit_hint) + cred);
        if (!current.getIsMuted()) {
            tts.speak(getString(R.string.intro_money_tts), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void anim() {
        ImageView image = findViewById(R.id.imgView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            if (TextUtils.isEmpty(addSum.getText()) && TextUtils.isEmpty(creditSum.getText())) {
                Toast.makeText(this, getString(R.string.check_fields), Toast.LENGTH_SHORT).show();
            } else {
                Double cashToAdd = Double.parseDouble(addSum.getText().toString());
                Double creditAdd = current.getCreditBalance();
                if (TextUtils.isEmpty(creditSum.getText())) {

                } else {
                    creditAdd = Double.parseDouble(creditSum.getText().toString());
                }

                addCash(cashToAdd, creditAdd);
            }
        }
    }
}
