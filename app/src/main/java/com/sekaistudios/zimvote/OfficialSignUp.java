package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.FingerprintGestureController;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class OfficialSignUp extends AppCompatActivity implements OnClickListener{
    //Declare Views
    private EditText editName, editID, editEmail, editPin;
    private Spinner spinSecLevel;
    private Button btnSignUp;
    private TextView txtLogin;
    private LottieAnimationView animLoading;

    //Final Vars
    private String name, id, email, pin, secLevel, uid, log;
    private Boolean status = Boolean.TRUE;


    //Declare Firebase
    private FirebaseAuth officialAuth;
    private FirebaseDatabase officialDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_sign_up);

        editName = (EditText)findViewById(R.id.editTextName);
        editID = (EditText)findViewById(R.id.editTextID);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editPin = (EditText) findViewById(R.id.editTextPin);
        btnSignUp = (Button)findViewById(R.id.buttonSignUp);
        txtLogin = (TextView) findViewById(R.id.textViewAccount);
        spinSecLevel = (Spinner)findViewById(R.id.spinnerSecLevel);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);

        //Firebase Instances
        officialAuth =  FirebaseAuth.getInstance();
        officialDB = FirebaseDatabase.getInstance();

        //Listeners
        btnSignUp.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }

    public boolean verifyVars(){
        //get values
        name = editName.getText().toString().trim();
        id = editID.getText().toString().trim();
        email = editEmail.toString().trim();
        pin = editPin.getText().toString().trim();
        secLevel = spinSecLevel.getSelectedItem().toString().trim();
        log = null;
        Boolean result;
        //Valiadte
        if (TextUtils.isEmpty(name)){
            editName.setError("Full Name Required");
            editName.requestFocus();
            result = Boolean.FALSE;
            return  result;
        }
        if (TextUtils.isEmpty(id) || !id.contains("-")){
            editID.setError("Invalid ID");
            editID.requestFocus();
            result = Boolean.FALSE;
            return  result;
        }
        if (TextUtils.isEmpty(email)){
            editEmail.setError("Full Name Required");
            editEmail.requestFocus();
            result = Boolean.FALSE;
            return  result;
        }
        if (TextUtils.isEmpty(pin)){
            editPin.setError("Full Name Required");
            editPin.requestFocus();
            result = Boolean.FALSE;
            return  result;
        }

        //make email
        email = editEmail.getText().toString().trim() + "@zec.zim";
        result = Boolean.TRUE;

        return result;
    }

    public void createAccount(){
        officialAuth.createUserWithEmailAndPassword(email,pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Set id
                    uid = officialAuth.getUid();
                    Toast.makeText(OfficialSignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
                    createDataPoints();
                }else if(!task.isSuccessful()){
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(OfficialSignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void createDataPoints(){
        //Create Class
        Official newOfficial = new Official(name, id, email, pin, secLevel, uid, log, status);

        //Make Ref
        DatabaseReference officialRef = officialDB.getReference("Officials Database");

        //Set DataPoints
        officialRef.child(uid).setValue(newOfficial).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(OfficialSignUp.this, "Redirecting...", Toast.LENGTH_SHORT).show();
                Intent toLogin = new Intent(OfficialSignUp.this, OfficialLogin.class);
                startActivity(toLogin);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == txtLogin){
            Intent toLogin = new Intent(this, OfficialLogin.class);
            startActivity(toLogin);
            finish();
        }

        if (v == btnSignUp){
            //Toast.makeText(this, "Not Coded Yet", Toast.LENGTH_SHORT).show();
            animLoading.setVisibility(View.VISIBLE);
            Boolean result  = verifyVars();

            if (result.equals(Boolean.TRUE)){
                createAccount();
            }else{
                animLoading.setVisibility(View.GONE);
                Toast.makeText(this, "Fix your Input", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
