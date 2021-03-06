package gq.rbeat.cashy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
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

import java.util.Locale;


public class Assistant extends AppCompatActivity {

    TextView textExample;
    ImageView imgView;
    Button add, spend, balance, bills, showBills, showSpends, logout, mute;
    TextToSpeech tts;
    DatabaseReference mDatabase;
    String name, email;
    User current;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale(getString(R.string.tts_lang)));
                }
            }
        });

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        setContentView(R.layout.activity_assistant2);
        add = findViewById(R.id.add);
        mute = findViewById(R.id.mute);
        textExample = findViewById(R.id.textExample);
        imgView = findViewById(R.id.imgView);
        spend = findViewById(R.id.spendBt);
        balance = findViewById(R.id.balanceBt);
        bills = findViewById(R.id.billsBt);
        logout = findViewById(R.id.logout);
        showBills = findViewById(R.id.showBillsBt);
        showSpends = findViewById(R.id.showSpendsBt);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                current.recalculateAvailable();
                mDatabase.child(email).setValue(current);
                name = current.getName();
                String welcomeScreen = getString(R.string.welcome) + " " + name + "!";
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopService(new Intent(Assistant.this, BackgroundSoundService.class));
                spend.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                mute.setVisibility(View.VISIBLE);
                balance.setVisibility(View.VISIBLE);
                showBills.setVisibility(View.VISIBLE);
                bills.setVisibility(View.VISIBLE);
                showSpends.setVisibility(View.VISIBLE);
                textExample.setText(welcomeScreen);
                animation.start();
                if (current.getAvailable() <= 35) {
                    balance.setTextColor(Color.RED);
                    balance.setText(getString(R.string.current_balance_warning));
                }
                if (!current.getIsMuted()) {
                    tts.speak(welcomeScreen + getString(R.string.intro_tts), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    mute.setText(getString(R.string.unmute_tts));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Assistant.this, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Assistant.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.logout))
                        .setMessage(getString(R.string.sure_logout))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
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
        showBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Assistant.this, ShowBills.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        showSpends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Assistant.this, ShowSpends.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.toggleTTS();
                mDatabase.child(email).setValue(current);
                finish();
                startActivity(getIntent());
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
