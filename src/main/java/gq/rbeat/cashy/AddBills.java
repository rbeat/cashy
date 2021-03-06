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

public class AddBills extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;

    TextView sum, name;
    Button add;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;

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
                anim();
                if (!current.getIsMuted()) {
                    tts.speak(getString(R.string.intro_bills_tts), TextToSpeech.QUEUE_FLUSH, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddBills.this, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_add_bills);
        add = findViewById(R.id.AddActionBt);
        sum = findViewById(R.id.sumBill);
        name = findViewById(R.id.nameBills);


        add.setOnClickListener(this);


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
            if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(sum.getText())) {
                Toast.makeText(this, getString(R.string.check_fields), Toast.LENGTH_SHORT).show();
            } else {
                Double sum1 = Double.parseDouble(sum.getText().toString());
                String name1 = name.getText().toString();
                current.addToPay(name1, sum1);
                mDatabase.child(email).setValue(current);
                Toast.makeText(this, getString(R.string.adding_to_db_toast), Toast.LENGTH_SHORT).show();
                tts.speak(getString(R.string.adding_to_db_tts), TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(AddBills.this, Assistant.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        }
    }
}
