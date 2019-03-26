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

import java.text.DecimalFormat;

public class BalanceScreen extends AppCompatActivity {

    TextView tv;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_screen);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        tv = findViewById(R.id.textBalance);

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

    public void continueBalance() {
        current.recalculateAvailable();
        Double balance = current.getPersonalBalance();

        Double credit = current.getCreditBalance();

        Double available = current.getAvailable();

        tv.setText("Balance: " + new DecimalFormat("##.##").format(balance) + "\nCredit: " + new DecimalFormat("##.##").format(credit) + "\nAvailable: " + new DecimalFormat("##.##").format(available));

    }


}
