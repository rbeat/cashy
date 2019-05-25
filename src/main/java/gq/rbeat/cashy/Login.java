package gq.rbeat.cashy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText email, password;
    Button signInBt, regBt, iForgot;
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
        iForgot = findViewById(R.id.iforgot);
        iForgot.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        regBt.setOnClickListener(this);
        anim(this);

    }

    public void anim(Login view) {
        ImageView image = findViewById(R.id.loginLogo);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about: {
                new AlertDialog.Builder(Login.this)
                        .setTitle("About")
                        .setMessage("Cashy: Your true money bro.\nCreated by Rodion Grinberg\nhttp://rbeat.gq\nadmin@rbeat.gq")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        }).show();
                return true;

            }
            case R.id.exit: {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Do you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;

            }
            default: {
                return super.onOptionsItemSelected(item);

            }
        }
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

        Toast.makeText(Login.this, "Registering...", Toast.LENGTH_SHORT).show();

        firebaseAuth.createUserWithEmailAndPassword(email_user, password_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                email.setEnabled(true);
                password.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                    signUser();
                    Intent intent = new Intent(Login.this, Setup.class);
                    intent.putExtra("email", md5(email_user));
                    startActivity(intent);

                } else {
                    Toast.makeText(Login.this, "Fail. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void iForgotAction() {
        final String email_user = email.getText().toString();
        if (TextUtils.isEmpty(email_user)) {
            Toast.makeText(this, "Enter your email and then, click \"Forgot password?\" again.", Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email_user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Check your email for password recovery instructions.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Failed. Check the email or try again later.", Toast.LENGTH_LONG).show();
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

        Toast.makeText(Login.this, "Signing in...", Toast.LENGTH_SHORT).show();

        firebaseAuth.signInWithEmailAndPassword(email_user, password_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                email.setEnabled(true);
                password.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Signed in successfully.", Toast.LENGTH_SHORT).show();
                    Intent service = new Intent(Login.this, BackgroundSoundService.class);
                    startService(service);
                    Intent intent = new Intent(Login.this, Assistant.class);
                    intent.putExtra("email", md5(email_user));

                    startActivity(intent);

                } else {
                    Toast.makeText(Login.this, "Failed. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == signInBt) {
            signUser();
        }

        if (v == iForgot) {
            iForgotAction();
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
