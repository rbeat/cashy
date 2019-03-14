package gq.rbeat.cashy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, password;
    Button signInBt, regBt;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.email);
        password = findViewById(R.id.email);
        signInBt = findViewById(R.id.signInBt);
        regBt = findViewById(R.id.regBt);
        signInBt.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        regBt.setOnClickListener(this);
    }

    public void registerUser() {
        final String email_user = email.getText().toString();
        String password_user = password.getText().toString();

        if (TextUtils.isEmpty(email_user) || TextUtils.isEmpty(password_user)) {
            Toast.makeText(this, "Please, check whether you entered all of the fields.", Toast.LENGTH_LONG).show();
            return;
        }
        email.setEnabled(false);
        password.setEnabled(false);

        Toast.makeText(MainActivity.this, "Registering...", Toast.LENGTH_SHORT).show();

        firebaseAuth.createUserWithEmailAndPassword(email_user, password_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                email.setEnabled(true);
                password.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                    signUser();
                    Intent intent = new Intent(MainActivity.this, Setup.class);
                    intent.putExtra("email", email_user);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Fail. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUser() {
        final String email_user = email.getText().toString();
        String password_user = password.getText().toString();

        if (TextUtils.isEmpty(email_user) || TextUtils.isEmpty(password_user)) {
            Toast.makeText(this, "Please, check whether you entered all of the fields.", Toast.LENGTH_LONG).show();
            return;
        }
        email.setEnabled(false);
        password.setEnabled(false);

        Toast.makeText(MainActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();

        firebaseAuth.signInWithEmailAndPassword(email_user, password_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                email.setEnabled(true);
                password.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Signed in successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Assistant.class);
                    intent.putExtra("email", email_user);

                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Failed. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == signInBt) {
            signUser();
        }

        if (v == regBt) {
            registerUser();
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
