package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

public class CandidatesManagement extends AppCompatActivity implements View.OnClickListener{
    //Parse vars
    private String electionID, campaignID, name;

    //Dec Vars
    private Button btnDelCampaign, btnCreateCandidate, btnResults;
    private ListView listCandidates;
    private LottieAnimationView animLoading;

    //Firebase
    private FirebaseDatabase candidatesDB, campaignsDB, partyDB;

    //TODO ListView Candidates
    private ArrayAdapter<Candidate> arrayAdapterCandidate;

    //TODO Candidate Create PopUp
    Dialog dialogCandidateCreate;
    private Spinner spinParty;
    private ArrayAdapter<Party> arrayAdapterParty;

    //TODO PopUp Views
    //Declare Views
    EditText editID1, editID2, editID3, editName, editPOB;
    DatePicker dateDOB;
    LottieAnimationView animLoading2;
    Button btnRegister;

    //TODO Candidate Final Vars
    String id1, id2, id3, finalid, finalname, finaldob, finalpob, finalParty;

    //TODO Data Flags
    //Data Flags
    private boolean flagVerifyID = false;
    private boolean candidateRefresh = false;


    //TODO Image Picker vArs
    //Voter PopUp
    Dialog dialogImagePicker;
    Candidate imagingCandidate;
    ImageView imgPreview;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String imageFileName;
    private Uri candidateImageUri;
    private StorageReference candidateImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates_management);
        //Get Bundle
        Bundle bundle = getIntent().getExtras();
        electionID = bundle.getString("electionID");
        campaignID = bundle.getString("campaignID");
        name = bundle.getString("campaignName");

        //Bind Vars
        btnDelCampaign = (Button)findViewById(R.id.buttonDeleteCampaign);
        btnCreateCandidate = (Button)findViewById(R.id.buttonCreateCandidate);
        listCandidates = (ListView)findViewById(R.id.listViewCandidates);
        btnDelCampaign.setText("DELETE: " + name);
        btnResults = (Button) findViewById(R.id.buttonViewResults);
        animLoading2 = (LottieAnimationView)findViewById(R.id.animationViewLoading);

        dialogCandidateCreate = new Dialog(this);

        //Firebase init
        candidatesDB = FirebaseDatabase.getInstance();
        campaignsDB = FirebaseDatabase.getInstance();
        partyDB = FirebaseDatabase.getInstance();
        candidateImageRef = FirebaseStorage.getInstance().getReference("CandidateImages");

        //TODO Campaign Adapter
        arrayAdapterCandidate = new ArrayAdapter<Candidate>(this, android.R.layout.simple_list_item_1);
        listCandidates.setAdapter(arrayAdapterCandidate);
        getCandidates();

        //TODO Party Adapter
        arrayAdapterParty = new ArrayAdapter<Party>(this,
                android.R.layout.simple_spinner_item);
        arrayAdapterParty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //OnClick
        btnResults.setOnClickListener(this);
        btnCreateCandidate.setOnClickListener(this);
        btnDelCampaign.setOnClickListener(this);
        dialogImagePicker = new Dialog(this);


        //ListClicks
        listCandidates.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                imagingCandidate = arrayAdapterCandidate.getItem(position);
                showCandidatePhotoPicker();
                return true;
            }
        });
    }
    //TODO Reg, Delete and View Functions

    public void getCandidates(){
        if (candidateRefresh == false){

        }else {
            if (!arrayAdapterCandidate.isEmpty()){
                arrayAdapterCandidate.clear();
            }
        }
        animLoading2.setVisibility(View.VISIBLE);
        //TODO Make Ref, get Candidates and Populate
        DatabaseReference candidateRef = candidatesDB.getReference("Candidates/" + electionID + "/" + campaignID);

        candidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Candidate currentCandidate = snapshot.getValue(Candidate.class);
                        arrayAdapterCandidate.add(currentCandidate);
                    }
                }else {
                    Toast.makeText(CandidatesManagement.this, "No Candidates", Toast.LENGTH_SHORT).show();
                    animLoading2.setVisibility(View.GONE);
                    return;
                }
                arrayAdapterCandidate.notifyDataSetChanged();
                animLoading2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showPopupCandidateCreate(){
        //Declare Views
        //EditText editID1, editID2, editID3, editName, editPOB;
        //DatePicker dateDOB;
        //LottieAnimationView animLoading2;
        //Button btnRegister;
        dialogCandidateCreate.setContentView(R.layout.popup_candidate_create);
        spinParty = (Spinner) dialogCandidateCreate.findViewById(R.id.spinnerParty);
        spinParty.setAdapter(arrayAdapterParty);


        //Bind
        editID1 = (EditText)dialogCandidateCreate.findViewById(R.id.editTextID1);
        editID2 = (EditText)dialogCandidateCreate.findViewById(R.id.editTextID2);
        editID3 = (EditText)dialogCandidateCreate.findViewById(R.id.editTextID3);
        editName = (EditText)dialogCandidateCreate.findViewById(R.id.editTextName);
        editPOB = (EditText)dialogCandidateCreate.findViewById(R.id.editTextPOB);
        dateDOB = (DatePicker)dialogCandidateCreate.findViewById(R.id.datePickerDOB);
        animLoading2 = (LottieAnimationView)dialogCandidateCreate.findViewById(R.id.animationViewLoading);
        btnRegister = (Button)dialogCandidateCreate.findViewById(R.id.buttonRegister);

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
                        //Assign Globals
                        id1 = editID1.getText().toString().trim();
                        id2 = editID2.getText().toString().trim();
                        id3 = editID3.getText().toString().trim();
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
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID2.getText().toString().trim().equals("") || editID2.getText().toString().trim().length() < 6){
                        editID2.requestFocus();
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID3.getText().toString().trim().equals("") || editID3.getText().toString().trim().length() < 3){
                        editID3.requestFocus();
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID2.getText().toString().trim().equals("") || editID2.getText().toString().trim().length() < 6){
                        editID2.requestFocus();
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editID3.getText().toString().trim().equals("") || editID3.getText().toString().trim().length() < 3){
                        editID3.requestFocus();
                        Toast.makeText(CandidatesManagement.this, "Finish ID First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        //TODO FOCUS HANDLIGNEND

        //OnClick
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalParty = spinParty.getSelectedItem().toString().trim();
                verifyExtras();
            }
        });

        //setData
        dialogCandidateCreate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCandidateCreate.show();
        getParties();
        //TODO End of Setup PopUp
    }

    public void verifyExtras(){
        animLoading2.setVisibility(View.VISIBLE);
        if (flagVerifyID == false){
            Toast.makeText(this, "ID not Verified", Toast.LENGTH_SHORT).show();
            verifyID();
            animLoading2.setVisibility(View.GONE);
            return;
        }

        //assign vars
        final String CanName = editName.getText().toString().trim();
        final String CanPob = editPOB.getText().toString().trim();

        //validate all other info
        if (TextUtils.isEmpty(CanName)){
            editName.setError("Enter Voter Name");
            editName.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(CanPob)){
            editPOB.setError("Enter Place of Birth");
            editPOB.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }

        //Get Date
        finaldob = String.valueOf(dateDOB.getDayOfMonth()) + "-" + String.valueOf(dateDOB.getMonth() + 1) + "-" +
                String.valueOf(dateDOB.getYear());
        finalname = editName.getText().toString().trim();
        finalpob = editPOB.getText().toString().trim();

        registerCandidate();
    }

    public void getParties(){
        if (!arrayAdapterParty.isEmpty()){
            arrayAdapterParty.clear();
        }
        //Make Ref
        DatabaseReference partyRef = partyDB.getReference("Parties");
        partyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Party currentParty = snapshot.getValue(Party.class);
                        arrayAdapterParty.add(currentParty);
                        arrayAdapterParty.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(CandidatesManagement.this, "No Parties Created.", Toast.LENGTH_SHORT).show();
                    Intent toParty = new Intent(CandidatesManagement.this, PartyManagement.class);
                    startActivity(toParty);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verifyID(){
        animLoading2.setVisibility(View.VISIBLE);
        flagVerifyID = false;

        //validate ID
        if (TextUtils.isEmpty( id1) || (id1.length() < 2)){
            editID1.setError("Enter 2 Digits");
            editID1.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty( id2) || (id2).length() < 6){
            editID2.setError("Enter at least 6 Digits");
            editID2.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty( id3) || (id3).length() < 3){
            editID3.setError("Enter 3 Chars");
            editID3.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isDigitsOnly(id3)){
            editID3.setError("At least 1 Letter required");
            editID3.requestFocus();
            animLoading2.setVisibility(View.GONE);
            return;
        }

        //assemble id
        finalid = editID1.getText().toString().trim() + "-" + editID2.getText().toString().trim()
                + " " + editID3.getText().toString().trim();

        //Ref
        DatabaseReference candidatesRef = candidatesDB.getReference("Candidates/" + electionID + "/"
                + campaignID);

        candidatesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Candidate currentCandidate = snapshot.getValue(Candidate.class);
                        if (currentCandidate.getId().equals(finalid)){
                            //Notify Result
                            animLoading2.setVisibility(View.GONE);
                            Toast.makeText(CandidatesManagement.this, "Candidate Exists Already",
                                    Toast.LENGTH_SHORT).show();
                            editID1.setText("");
                            editID2.setText("");
                            editID3.setText("");
                            editID1.requestFocus();
                            return;
                        }
                    }
                    flagVerifyID = true;
                    editName.requestFocus();
                    animLoading2.setVisibility(View.GONE);
                }else {
                    editName.requestFocus();
                    flagVerifyID = true;
                    animLoading2.setVisibility(View.GONE);
                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void registerCandidate(){
        //TODO Get Values
        //Make Class
        Candidate newCandidate = new Candidate(electionID, campaignID, finalid, finalname, finaldob, finalpob,
                finalParty, 0, null, null, true);

        //Make Ref
        DatabaseReference candidateRef = candidatesDB.getReference("Candidates/" + electionID + "/" + campaignID);

        candidateRef.child(finalid).setValue(newCandidate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CandidatesManagement.this, "Candidate Created", Toast.LENGTH_SHORT).show();
                    editID1.setText("");
                    editID2.setText("");
                    editID3.setText("");
                    editName.setText("");
                    editPOB.setText("");
                    candidateRefresh = true;
                    getCandidates();
                    dialogCandidateCreate.dismiss();
                    animLoading2.setVisibility(View.GONE);
                }else if (!task.isSuccessful()){
                    Toast.makeText(CandidatesManagement.this, "Error, try again", Toast.LENGTH_SHORT).show();
                    animLoading2.setVisibility(View.GONE);
                }
            }
        });
    }

    public void deleteCampaign(){
        //Make Path
        animLoading2.setVisibility(View.VISIBLE);
        //TODO DeleteImages
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("CandidateImages/"
                + campaignID);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CandidatesManagement.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //TODO DeleteVotes
        DatabaseReference votesRef = candidatesDB.getReference("Votes/" + campaignID);
        votesRef.removeValue();
        //TODO Delete Candidates
        DatabaseReference candidateRef = candidatesDB.getReference("Candidates/" + electionID + "/" + campaignID);
        candidateRef.removeValue();
        //TODO Delete Campaigns
        DatabaseReference campaignsRef = candidatesDB.getReference("Campaigns/" + electionID + "/" + campaignID);
        campaignsRef.removeValue();

        Intent toCampaignManagement = new Intent(CandidatesManagement.this, CampaignsManagement.class);
        startActivity(toCampaignManagement);
        finish();
    }

    //TODO Image Fucntions

    public void showCandidatePhotoPicker(){
        //Make Vars
        TextView txtName;
        Button btnPickImage, btnUploadImage;

        //SetView
        dialogImagePicker.setContentView(R.layout.popup_candidate_image_pick);

        //Bind Vars
        txtName = (TextView) dialogImagePicker.findViewById(R.id.textViewCandidateName);
        imgPreview = (ImageView) dialogImagePicker.findViewById(R.id.imageViewImagePreview);
        btnPickImage = (Button) dialogImagePicker.findViewById(R.id.buttonPickImage);
        btnUploadImage = (Button)dialogImagePicker.findViewById(R.id.buttonUploadImage);

        //Set Download Data
        txtName.setText(imagingCandidate.getName());

        //Start Dialog
        dialogImagePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogImagePicker.show();

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private void openFileChooser(){
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            candidateImageUri = data.getData();

            Picasso.with(this).load(candidateImageUri).into(imgPreview);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        animLoading2.setVisibility(View.VISIBLE);
        if (candidateImageUri != null){
            final String candidateID = imagingCandidate.getId();
            final String candidateCampaignID = imagingCandidate.getCampaignID();

            StorageReference fileRef = candidateImageRef.child(candidateCampaignID).
                    child((candidateID) + "." + getFileExtension(candidateImageUri));

            fileRef.putFile(candidateImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CandidatesManagement.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    dialogImagePicker.dismiss();
                    animLoading2.setVisibility(View.GONE);
                }
            });
        }else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
            animLoading2.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnCreateCandidate){
            //TODO Open PopUp
            //TODO On id entry, check existance
            //TODO ON Click, Verify Data, procees
            //TODO Create Voter
            showPopupCandidateCreate();
        }

        if (v == btnDelCampaign){
            //TODO Make Path
            //TODO Delete Command
            deleteCampaign();
        }

        if (v == btnResults){
            Intent toResults = new Intent(CandidatesManagement.this, RealTimeResults.class);
            toResults.putExtra("campaignID", campaignID);
            toResults.putExtra("electionID", electionID);
            startActivity(toResults);
        }
    }
}
