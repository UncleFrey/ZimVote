package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

public class VoterValidation extends AppCompatActivity implements View.OnClickListener{
    //TODO Get Data
    //camp1
    private String camp1Election, camp1Camp;
    //camp2
    private String camp2Election, camp2Camp;
    //camp3
    private String camp3Election, camp3Camp;

    //Dec Vars
    private EditText editID1, editID2, editID3;
    private Button btnVerify;

    //Firebase
    private FirebaseDatabase voterDB, vvDB;

    //Upload Data
    private String id;

    //Data Flags
    private boolean flagVerifyID = false;
    //Download Data
    private String voterName, voterID, voterDOB, voterPOB;

    private boolean validationResult = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_validation);
        //Get Values
        Bundle bundle = getIntent().getExtras();
        camp1Election = bundle.getString("camp1Election");
        camp1Camp = bundle.getString("camp1Camp");
        camp2Election = bundle.getString("camp2Election");
        camp2Camp = bundle.getString("camp2Camp");
        camp3Election = bundle.getString("camp3Election");
        camp3Camp = bundle.getString("camp3Camp");

        //Bind
        editID1 = (EditText)findViewById(R.id.editTextID1);
        editID2 = (EditText)findViewById(R.id.editTextID2);
        editID3 = (EditText)findViewById(R.id.editTextID3);
        btnVerify = (Button)findViewById(R.id.buttonVerify);
        //Firebase
        voterDB = FirebaseDatabase.getInstance();
        vvDB = FirebaseDatabase.getInstance();

        btnVerify.setOnClickListener(this);

        //TODO FOCUS HANDLING
        editID2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if(editID1.getText().toString().trim().equals("") || editID1.getText().toString().trim().length() <2){
                        editID1.requestFocus();
                        return;
                    }
                }
            }
        });

        editID3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if(editID1.getText().toString().trim().equals("") || editID1.getText().toString().trim().length() <2){
                        editID1.requestFocus();
                        return;
                    }

                    if (editID2.getText().toString().trim().equals("") || editID2.getText().toString().trim().length() < 6){
                        editID2.requestFocus();
                        return;
                    }
                }else if(!hasFocus){
                    if (editID1.getText().toString().trim().equals("") || editID2.getText().toString().trim().equals("")){

                    }else {

                    }
                }
            }
        });


    }

    public void verifyID(){
        Toast.makeText(this,"Verifying ID",Toast.LENGTH_SHORT).show();
        flagVerifyID = false;

        //assemble id
        id = editID1.getText().toString().trim() + "-" + editID2.getText().toString().trim()+ " " +
                editID3.getText().toString().trim();

        //validate ID
        if (TextUtils.isEmpty( editID1.getText().toString().trim()) || (editID1.getText().toString().trim()).length() < 2){
            editID1.setError("Enter 2 Digits");
            editID1.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( editID2.getText().toString().trim()) || (editID2.getText().toString().trim()).length() < 6){
            editID2.setError("Enter at least 6 Digits");
            editID2.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( editID3.getText().toString().trim()) || (editID3.getText().toString().trim()).length() < 3){
            editID3.setError("Enter 3 Chars");
            editID3.requestFocus();
            return;
        }
        if (TextUtils.isDigitsOnly(editID3.getText().toString().trim())){
            editID3.setError("At least 1 Letter required");
            editID3.requestFocus();
            return;
        }

        voterID = id;

        //Ref
        DatabaseReference voterRef = voterDB.getReference("Voters Database");
        //MakeRef


        voterRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (final DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Voter currentVoter = snapshot.getValue(Voter.class);
                        if (currentVoter.getId().equals(id)){
                            //TODO Test Verification
                            //Download Voter Data
                            voterName = currentVoter.getName();
                            voterID = currentVoter.getId();
                            voterDOB = currentVoter.getDob();
                            voterPOB = currentVoter.getPob();
                            checkVerification();
                            return;
                        }
                    }
                    Toast.makeText(VoterValidation.this, "Not Registered", Toast.LENGTH_LONG).show();
                    finish();
                    //TODO Sound Alarm
                    return;
                }else {
                    Toast.makeText(VoterValidation.this, "No Voters in DB!", Toast.LENGTH_SHORT).show();
                    flagVerifyID = true;
                    finish();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkVerification(){
        DatabaseReference vvRef = vvDB.getReference("Voters Database/"
                + voterID + "/voterVoteVerification");

        vvRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Toast.makeText(VoterValidation.this, snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    VoterVerification currentVerification = snapshot.getValue(VoterVerification.class);
                    final String testElection = currentVerification.getElectionID();
                    if (testElection.equals(camp1Election) || testElection.equals(camp2Election)
                            || testElection.equals(camp3Election)){
                        Toast.makeText(VoterValidation.this, "NO DOUBLE VOTING ALLOWED", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }else {

                    }
                }
                //Goto Voter Validation
                Intent toVote = new Intent(VoterValidation.this, eBallot.class);
                toVote.putExtra("camp1Camp", camp1Camp);
                toVote.putExtra("camp1Election", camp1Election);
                toVote.putExtra("camp2Camp", camp2Camp);
                toVote.putExtra("camp2Election", camp2Election);
                toVote.putExtra("camp3Camp", camp3Camp);
                toVote.putExtra("camp3Election", camp3Election);
                toVote.putExtra("voterID", voterID);
                toVote.putExtra("voterName", voterName);
                startActivity(toVote);
                finish();
                //TODO To Intent
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnVerify){
            verifyID();
        }

    }
}
