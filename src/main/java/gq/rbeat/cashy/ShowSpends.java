package gq.rbeat.cashy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
    String email;
    User current;
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
                getBills();
                tts.speak("Here's what you spent your money on.", TextToSpeech.QUEUE_FLUSH, null);
                anim();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowSpends.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
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
        listView = findViewById(R.id.listBills);
        adapter = new PaymentsAdapter(this, current.getPayment());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                new AlertDialog.Builder(ShowSpends.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.removePayment(position);
                                mDatabase.child(email).setValue(current);
                                Toast.makeText(ShowSpends.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

    }
}
