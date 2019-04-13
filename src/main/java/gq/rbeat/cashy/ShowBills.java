package gq.rbeat.cashy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowBills extends AppCompatActivity {

    TextView tv;
    String email;
    User current;
    TextToSpeech tts;
    ImageView imgView;
    private ListView listView;
    private BillsAdapter adapter;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bills);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        tv = findViewById(R.id.textBalance);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child(email).getValue(User.class);
                getBills();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowBills.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getBills() {
        listView = findViewById(R.id.listBills);
        adapter = new BillsAdapter(this, current.getToPay());
        listView.setAdapter(adapter);

    }
}
