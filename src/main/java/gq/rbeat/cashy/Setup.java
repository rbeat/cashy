package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.Locale;

public class Setup extends AppCompatActivity implements View.OnClickListener {

    Button ok_name;
    EditText name, balance, credit;
    String email;
    private DatabaseReference mDatabase;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ok_name = findViewById(R.id.ok_name);
        name = findViewById(R.id.name);
        balance = findViewById(R.id.balance);
        credit = findViewById(R.id.credit);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ok_name.setOnClickListener(this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale("en_US"));
                }
            }
        });
        tts.speak("Hey! Welcome to Cashy! Tell me, what's your name, and your current balance, so we could proceed.", TextToSpeech.QUEUE_FLUSH, null);

    }

    public void createUser(String name, double balance, double creditBalance) {
        User user = new User(name, email);
        user.setPersonalBalance(balance);
        user.setCreditBalance(creditBalance);
        mDatabase.child("Users").child(md5(email)).setValue(user);
        Intent intent = new Intent(Setup.this, Assistant.class);
        intent.putExtra("email", md5(email));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == ok_name) {
            createUser(name.getText().toString(), Double.parseDouble(balance.getText().toString()), Double.parseDouble(credit.getText().toString()));
        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }
}
