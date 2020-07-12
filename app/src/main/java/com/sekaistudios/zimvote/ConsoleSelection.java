package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ConsoleSelection extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private LottieAnimationView animVoterReg, animZecAdmin, animPoll, animResults;
    private TextView txtLogout;
    private ImageView imgLogo;

    //Bck to exit
    private long backPressedTime;

    //Firebase
    private FirebaseAuth officialAuth;
    private static int SPLASH_TIME_OUT = 8000;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_selection);

        //Bind Views
        animVoterReg = (LottieAnimationView) findViewById(R.id.animationViewVoterReg);
        animZecAdmin = (LottieAnimationView) findViewById(R.id.animationViewZecAdmin);
        animPoll = (LottieAnimationView)findViewById(R.id.animationViewVotingPoll);
        animResults = (LottieAnimationView)findViewById(R.id.animationViewResults);
        txtLogout = (TextView)findViewById(R.id.textViewLogout);
        imgLogo = (ImageView)findViewById(R.id.imageViewAppLogo) ;

        officialAuth = FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide Assets
                imgLogo.setVisibility(View.GONE);
            }
        }, SPLASH_TIME_OUT);


        //setOnClick
        animVoterReg.setOnClickListener(this);
        animZecAdmin.setOnClickListener(this);
        animPoll.setOnClickListener(this);
        animResults.setOnClickListener(this);
        txtLogout.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        if (v == animVoterReg){
            //Goto Voter Reg
            Intent toVoterReg = new Intent(ConsoleSelection.this, VoterRegistration.class);
            startActivity(toVoterReg);
        }

        if (v == animZecAdmin){
            //Goto ZEC Centre
            Intent toZec = new Intent(ConsoleSelection.this, ZecAdminConsole.class);
            startActivity(toZec);
        }

        if(v == animPoll){
            //Goto Setup
            Intent toSetup = new Intent(ConsoleSelection.this, CampaignsManagement.class);
            startActivity(toSetup);
        }

        if (v == animResults){
            //GotoResults
        }

        if (v == txtLogout){
            officialAuth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent toLogin = new Intent(ConsoleSelection.this, OfficialLogin.class);
            startActivity(toLogin);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }


    }
}
