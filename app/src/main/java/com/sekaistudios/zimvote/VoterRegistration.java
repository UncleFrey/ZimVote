package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.FingerprintDialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VoterRegistration extends AppCompatActivity implements View.OnClickListener{
    //DecViews
    private EditText editID1, editID2, editID3, editName, editPOB;
    private DatePicker dateDOB;
    private Button btnRegVoter;
    private LottieAnimationView animLoading;

    //Firebase
    private FirebaseDatabase voterDB;

    //Upload Data
    private String id, name, dob, pob, verification = null, log = null;
    private Boolean status = Boolean.TRUE;


    //Data Flags
    private boolean flagVerifyID = false;
    //Download Data
    private String voterName, voterID, voterDOB, voterPOB;

    //Voter PopUp
    Dialog dialogVoter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_registration);

        //Bind
        editID1 = (EditText)findViewById(R.id.editTextID1);
        editID2 = (EditText)findViewById(R.id.editTextID2);
        editID3 = (EditText)findViewById(R.id.editTextID3);
        editName = (EditText)findViewById(R.id.editTextName);
        editPOB = (EditText)findViewById(R.id.editTextPOB);
        dateDOB = (DatePicker)findViewById(R.id.datePickerDOB);
        btnRegVoter = (Button)findViewById(R.id.buttonRegister);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);

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
                        verifyID();
                    }
                }
            }
        });

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if(editID1.getText().toString().trim().equals("") || editID1.getText().toString().trim().length() <2){
                        editID1.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID2.getText().toString().trim().equals("") || editID2.getText().toString().trim().length() < 6){
                        editID2.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID3.getText().toString().trim().equals("") || editID3.getText().toString().trim().length() < 3){
                        editID3.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        editPOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if(editID1.getText().toString().trim().equals("") || editID1.getText().toString().trim().length() <2){
                        editID1.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID2.getText().toString().trim().equals("") || editID2.getText().toString().trim().length() < 6){
                        editID2.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID3.getText().toString().trim().equals("") || editID3.getText().toString().trim().length() < 3){
                        editID3.requestFocus();
                        Toast.makeText(VoterRegistration.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        //TODO FOCUS HANDLIGNEND

        dialogVoter = new Dialog(this);

        //Firebase init
        voterDB = FirebaseDatabase.getInstance();

        //OnClick
        btnRegVoter.setOnClickListener(this);
    }

    public void showVoterPopup(){
        //Make Vars
        TextView txtName, txtID, txtDOB, txtPOB;
        Button btnDel;

        //SetView
        dialogVoter.setContentView(R.layout.popup_voter_view);

        //Bind Vars
        txtName = (TextView) dialogVoter.findViewById(R.id.textViewName);
        txtID = (TextView) dialogVoter.findViewById(R.id.textViewID);
        txtDOB = (TextView) dialogVoter.findViewById(R.id.textViewDOB);
        txtPOB = (TextView) dialogVoter.findViewById(R.id.textViewPOB);
        btnDel = (Button) dialogVoter.findViewById(R.id.buttonDelete);

        //Set Download Data
        txtName.setText(voterName);
        txtID.setText(voterID);
        txtDOB.setText(voterDOB);
        txtPOB.setText(voterPOB);

        //Make Reference
        final DatabaseReference voterRef = voterDB.getReference("Voters Database/" + voterID);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voterRef.removeValue();
                Toast.makeText(VoterRegistration.this, "Deleted", Toast.LENGTH_SHORT).show();
                dialogVoter.dismiss();
            }
        });

        //Start Dialog
        dialogVoter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVoter.show();

        //Cleat Junk
        editID1.setText("");
        editID2.setText("");
        editID3.setText("");
        editName.setText("");
        editPOB.setText("");

        animLoading.setVisibility(View.GONE);
    }

    public void verifyID(){
        animLoading.setVisibility(View.VISIBLE);
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

        voterRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Voter currentVoter = snapshot.getValue(Voter.class);
                        if (currentVoter.getId().equals(id)){
                            //Download Voter Data
                            voterName = currentVoter.getName();
                            voterID = currentVoter.getId();
                            voterDOB = currentVoter.getDob();
                            voterPOB = currentVoter.getPob();
                            //Notify Result
                            Toast.makeText(VoterRegistration.this, "Voter Exists Already",
                                    Toast.LENGTH_SHORT).show();
                            showVoterPopup();
                            editID1.requestFocus();
                            animLoading.setVisibility(View.GONE);
                            return;
                        }
                    }

                    Toast.makeText(VoterRegistration.this, "Verified!", Toast.LENGTH_SHORT).show();
                    editName.requestFocus();
                    editID1.requestFocus();
                    animLoading.setVisibility(View.GONE);
                    flagVerifyID = true;
                }else {
                    editName.requestFocus();
                    Toast.makeText(VoterRegistration.this, "Verified!", Toast.LENGTH_SHORT).show();
                    flagVerifyID = true;
                    animLoading.setVisibility(View.GONE);
                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void verifyExtras(){

        if (flagVerifyID == false){
            Toast.makeText(this, "ID not Verified", Toast.LENGTH_SHORT).show();
            verifyID();
            return;
        }

        //assign vars
        name = editName.getText().toString().trim();
        pob = editPOB.getText().toString().trim();
        //make and get dob
        dob = String.valueOf(dateDOB.getDayOfMonth()) + "-" + String.valueOf(dateDOB.getMonth() + 1) + "-" +
                String.valueOf(dateDOB.getYear());

        //validate all other info
        if (TextUtils.isEmpty(name)){
            editName.setError("Enter Voter Name");
            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pob)){
            editPOB.setError("Enter Place of Birth");
            editPOB.requestFocus();
            return;
        }

        registerVoter();
    }

    public void registerVoter(){
        animLoading.setVisibility(View.VISIBLE);
        //Make Reference
        DatabaseReference voterRef = voterDB.getReference("Voters Database/" + id);
        //Make Class

        Voter newVoter = new Voter(id, name, dob, pob, null, log, status);

        voterRef.setValue(newVoter).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(VoterRegistration.this, "Registered", Toast.LENGTH_SHORT).show();
                    //Clear Fields
                    editID1.setText("");
                    editID2.setText("");
                    editID3.setText("");
                    editName.setText("");
                    editPOB.setText("");
                    animLoading.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegVoter){
            verifyExtras();
        }
    }
}
