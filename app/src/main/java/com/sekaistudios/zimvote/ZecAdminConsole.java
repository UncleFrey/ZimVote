package com.sekaistudios.zimvote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class ZecAdminConsole extends AppCompatActivity implements View.OnClickListener{
    //Declare Vars
    private LottieAnimationView animElections, animCampaigns, animCandidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zec_admin_console);
        //Bind Values
        animElections = (LottieAnimationView)findViewById(R.id.animationViewElection);
        animCampaigns = (LottieAnimationView)findViewById(R.id.animationViewCampaigns);
        animCandidates = (LottieAnimationView)findViewById(R.id.animationViewParty);

        //SetOnClick
        animElections.setOnClickListener(this);
        animCampaigns.setOnClickListener(this);
        animCandidates.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == animElections){
            //TODO Goto Elections
            Intent toElections = new Intent(ZecAdminConsole.this, ElectionsManagement.class);
            startActivity(toElections);
        }

        if (v == animCampaigns){
            //TODO Goto Campaigns
            Intent toCampaigns = new Intent(ZecAdminConsole.this, CampaignsManagement.class);
            startActivity(toCampaigns);
        }

        if (v == animCandidates){
            //TODO Goto Candidates
            Intent toParty = new Intent(ZecAdminConsole.this, PartyManagement.class);
            startActivity(toParty);
        }

    }
}
