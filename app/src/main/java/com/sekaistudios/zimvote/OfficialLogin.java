package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class OfficialLogin extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private EditText editEmail, editPin;
    private Button btnLogin;
    private TextView txtSignUp;
    private LottieAnimationView animLoading;


    //final VArs
    String email, pin;

    //Firebase
    private FirebaseAuth officialAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_login);

        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editPin = (EditText) findViewById(R.id.editTextPin);
        btnLogin = (Button)findViewById(R.id.buttonLogin);
        txtSignUp = (TextView) findViewById(R.id.textViewNoAccount);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading2);

        //Firebase Iniy
        officialAuth = FirebaseAuth.getInstance();

        //Listeners
        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
    }

    public Boolean verityVars(){
        //Get Vars
        email = editEmail.getText().toString().trim();
        pin = editPin.getText().toString().trim();

        //Test Vars
        if (TextUtils.isEmpty(email)){
            editEmail.setError("InputEmail address");
            editEmail.requestFocus();
            return Boolean.FALSE;
        }
        if (TextUtils.isEmpty(pin)){
            editPin.setError("Pin is Required");
            editPin.requestFocus();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void login(){
        animLoading.setVisibility(View.VISIBLE);
        officialAuth.signInWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //New Intent
                    Toast.makeText(OfficialLogin.this, "Logged In", Toast.LENGTH_SHORT).show();
                    Intent toConsole = new Intent(OfficialLogin.this, ConsoleSelection.class);
                    startActivity(toConsole);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else if(!task.isSuccessful()){
                    //Error
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(OfficialLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        animLoading.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == txtSignUp){
            Intent toSignUp = new Intent(this, OfficialSignUp.class);
            startActivity(toSignUp);
        }

        if (v == btnLogin){
            //Toast.makeText(this, "Not Coded Yet", Toast.LENGTH_SHORT).show();
            Boolean result = verityVars();

            if (result.equals(Boolean.TRUE)){
                login();
            }else if(result.equals(Boolean.FALSE)){
                Toast.makeText(this, "Fix Inputs", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
