package gq.rbeat.cashy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        getSupportActionBar().hide();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                if (!current.getIsMuted()) {
                    tts.speak(getString(R.string.intro_spend_money_tts), TextToSpeech.QUEUE_FLUSH, null);
                }
                anim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Spend.this, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend);

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
                    tts.setLanguage(new Locale(getString(R.string.tts_lang)));
                }
            }
        });

    }

    public void spend(Double sum, String text) {
        current.makePayment(text + " / " + getDateTime(), sum);
        mDatabase.child(email).setValue(current);
        Intent intent = new Intent(Spend.this, Assistant.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == confirm) {
            Toast.makeText(this, getString(R.string.adding_to_db_toast), Toast.LENGTH_SHORT).show();
            if (!current.getIsMuted()) {
                tts.speak(getString(R.string.adding_to_db_tts), TextToSpeech.QUEUE_FLUSH, null);
            }
            spend(Double.parseDouble(sumItem.getText().toString()), nameItem.getText().toString());
        }
        if (v == spendActionBt) {
            if (TextUtils.isEmpty(sumItem.getText()) || TextUtils.isEmpty(nameItem.getText())) {
                Toast.makeText(this, getString(R.string.check_fields), Toast.LENGTH_SHORT).show();
            } else {
                if (current.getAvailable() > Double.parseDouble(sumItem.getText().toString())) {
                    if (current.getPersonalBalance() > Double.parseDouble(sumItem.getText().toString())) {
                        Toast.makeText(this, getString(R.string.adding_to_db_toast), Toast.LENGTH_SHORT).show();
                        if (!current.getIsMuted()) {
                            tts.speak(getString(R.string.adding_to_db_tts), TextToSpeech.QUEUE_FLUSH, null);
                        }
                        spend(Double.parseDouble(sumItem.getText().toString()), nameItem.getText().toString());
                    } else {
                        hideKeyboard(this);
                        anim();
                        spendText1.setText(R.string.out_of_money_tv);
                        if (!current.getIsMuted()) {
                            tts.speak(getString(R.string.out_of_money_tts), TextToSpeech.QUEUE_FLUSH, null);
                        }
                        imgView.setImageResource(R.drawable.confused);
                        spendText2.setVisibility(View.GONE);
                        spendActionBt.setVisibility(View.GONE);
                        sumItem.setVisibility(View.GONE);
                        nameItem.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                    }
                } else {
                    anim();
                    Toast.makeText(this, getString(R.string.not_enough_money), Toast.LENGTH_SHORT).show();
                    if (!current.getIsMuted()) {
                        tts.speak(getString(R.string.not_enough_money), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    imgView.setImageResource(R.drawable.angry);
                }
            }
        }
    }

    public void anim() {
        ImageView image = findViewById(R.id.imgView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View f = activity.getCurrentFocus();
        if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
            imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
        else
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
