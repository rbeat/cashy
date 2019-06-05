package gq.rbeat.cashy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ShowSpends extends AppCompatActivity {

    TextView tv;
    EditText search;
    String email;
    User current;
    Payment payment;
    TextToSpeech tts;
    ImageView imgView;
    private ListView listView;
    private PaymentsAdapter adapter;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_spends);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        listView = findViewById(R.id.listBills);
        search = findViewById(R.id.search);
        tv = findViewById(R.id.textBalance);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale(getString(R.string.tts_lang)));
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                getBills();
                if (!current.getIsMuted()) {
                    tts.speak(getString(R.string.intro_show_spends_tts), TextToSpeech.QUEUE_FLUSH, null);
                }
                anim();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowSpends.this, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) {
                    payment = current.getPayment();
                }
                Payment temp = new Payment();
                String str = s.toString();
                for (int i = 0; i < current.getPayment().getName().size(); i++) {
                    if (current.getPayment().getName().get(i).toLowerCase().contains(str.toLowerCase())) {
                        temp.add(current.getPayment().getName().get(i), current.getPayment().getSum().get(i));
                    }
                }
                payment = temp;
                adapter = new PaymentsAdapter(ShowSpends.this, payment);
                listView.setAdapter(adapter);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void anim() {
        ImageView image = findViewById(R.id.imgView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    public void getBills() {
        payment = current.getPayment();
        adapter = new PaymentsAdapter(this, payment);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                new AlertDialog.Builder(ShowSpends.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.sure_delete))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.removePayment(payment.getName().get(position));
                                mDatabase.child(email).setValue(current);
                                Toast.makeText(ShowSpends.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

            }
        });

    }
}
