package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CampaignsManagement extends AppCompatActivity implements View.OnClickListener{

    //Declare Views
    private Spinner spinElection, spinType, spinProvince, spinDistrict;
    private TextView textViewCampaignCounter;
    private Button btnStartRealtime, btnClearRealTime;
    private EditText editRegion, editWard;
    private LottieAnimationView animLoading;

    private Button btnSearch, btnCreate;

    //Firebase
    private FirebaseDatabase electionsDB;
    private FirebaseDatabase campaignsDB;

    //Query Campaign Values
    private String campaignElectionID, campaignID, campaignType, campaignProvince, campaignDistrict,
    campaignRegion, campaignWard;

    //return Campaign Values
    private String returnElectionID, returnID, returnType, returnProvince, returnDistrict,
            returnRegion, returnWard;

    //TODO Elections Array and Adapters
    private ArrayList<String> arrayListElectionsID = new ArrayList<>();
    private ArrayAdapter<Election> arrayAdapterElections;

    //TODO Campaigns Array and Adapters
    private ArrayList<String> arrayListCampaignsID = new ArrayList<>();
    private ArrayAdapter<Campaign> arrayAdapterCampaigns;
    private ArrayList<String> arrayListCampaignListings = new ArrayList<>();


    //TODO Districy Array Adapters
    private ArrayAdapter<String> arrayAdapterDistrict;

    //TODO Campaigns PopUp
    Dialog dialogCampaigns;
    private ListView listCampaigns;

    //TODO Tracking Vars
    private boolean search = false;

    //TODO RunTime Storage
    private ArrayList<Campaign> arrayListRunTimeCampaigns = new ArrayList<>();
    //camp1
    private String camp1Election, camp1Camp;
    //camp2
    private String camp2Election, camp2Camp;
    //camp3
    private String camp3Election, camp3Camp;
    private int campaignCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaigns_management);

        //Binding
        spinElection = (Spinner)findViewById(R.id.spinnerElection);
        spinType = (Spinner)findViewById(R.id.spinnerCampaignType);
        spinProvince = (Spinner)findViewById(R.id.spinnerProvince);
        spinDistrict = (Spinner)findViewById(R.id.spinnerDistrict);
        editRegion = (EditText)findViewById(R.id.editTextRegion);
        editWard = (EditText)findViewById(R.id.editTextWard);
        textViewCampaignCounter = (TextView)findViewById(R.id.textViewRuntimeCounter);
        btnStartRealtime = (Button)findViewById(R.id.buttonStartRunTime);
        btnClearRealTime = (Button)findViewById(R.id.buttonClearRuntime);
        btnCreate = (Button)findViewById(R.id.buttonCreate);
        btnSearch = (Button)findViewById(R.id.buttonSearch);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);

        dialogCampaigns = new Dialog(this);

        editWard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (TextUtils.isEmpty(editRegion.getText().toString().trim())){
                        editRegion.requestFocus();
                    }
                }
            }
        });

        //Firebase Init
        electionsDB = FirebaseDatabase.getInstance();
        campaignsDB = FirebaseDatabase.getInstance();

        //TODO Elections Adapter Init
        arrayAdapterElections = new ArrayAdapter<Election>(this,
                android.R.layout.simple_spinner_item);
        arrayAdapterElections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setData
        spinElection.setAdapter(arrayAdapterElections);

        //TODO Get Current Elections
        updateElections();

        //TODO PopUp Adapter
        arrayAdapterCampaigns = new ArrayAdapter<Campaign>(this,
                android.R.layout.simple_list_item_1);

        //TODO PopUp Adapter

        //Set OnClick
        btnSearch.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnClearRealTime.setOnClickListener(this);
        btnStartRealtime.setOnClickListener(this);


        //TODO Spinner Change
        spinProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.harareDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 1){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.manicalandDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 2){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.mashonalandCentralDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 3){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.mashonalandEastDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 4){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.mashonalandWestDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 5){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.masvingoDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 6){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.matebelelandNorthDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 7){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.matebelelandSouthDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                if (position == 8){
                    arrayAdapterDistrict = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.midlandsDistricts));
                    arrayAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                spinDistrict.setAdapter(arrayAdapterDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    spinProvince.setEnabled(false);
                    spinDistrict.setEnabled(false);
                    editRegion.setEnabled(false);
                    editWard.setEnabled(false);
                }

                if (position == 1){
                    spinProvince.setEnabled(true);
                    spinDistrict.setEnabled(true);
                    editRegion.setEnabled(true);
                    editWard.setEnabled(false);
                }

                if (position == 2){
                    spinProvince.setEnabled(true);
                    spinDistrict.setEnabled(true);
                    editRegion.setEnabled(true);
                    editWard.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showPopupCampaigns(){
        dialogCampaigns.setContentView(R.layout.popup_campaigns_query);
        listCampaigns = (ListView) dialogCampaigns.findViewById(R.id.listViewCampaigns);
        listCampaigns.setAdapter(arrayAdapterCampaigns);

        //setData
        dialogCampaigns.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCampaigns.show();
        //TODO End of Setup PopUp

        //OnClick
        listCampaigns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get data sets
                String ele, camp, name;
                ele = campaignElectionID;
                //get camp
                camp = arrayListCampaignsID.get(position);
                name= arrayListCampaignListings.get(position);

                //NewIntent
                Intent toCandidateManager = new Intent(CampaignsManagement.this, CandidatesManagement.class);
                toCandidateManager.putExtra("electionID", ele);
                toCandidateManager.putExtra("campaignID", camp);
                toCandidateManager.putExtra("campaignName", name);
                startActivity(toCandidateManager);
                dialogCampaigns.dismiss();
            }
        });

        //OnHold
        listCampaigns.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (campaignCount <= 3){
                    //textCount
                    if (campaignCount == 1){
                        camp1Camp = arrayListCampaignsID.get(position);
                        camp1Election = campaignElectionID;
                        campaignCount = campaignCount + 1;
                        Toast.makeText(CampaignsManagement.this, "Added Campaign 1", Toast.LENGTH_SHORT).show();
                    }else if (campaignCount == 2){
                        camp2Camp = arrayListCampaignsID.get(position);
                        camp2Election = campaignElectionID;
                        campaignCount = campaignCount + 1;
                        Toast.makeText(CampaignsManagement.this, "Added Campaign 2", Toast.LENGTH_SHORT).show();
                    }else if (campaignCount == 3){
                        camp3Camp = arrayListCampaignsID.get(position);
                        camp3Election = campaignElectionID;
                        campaignCount = campaignCount + 1;
                        Toast.makeText(CampaignsManagement.this, "Added Campaign 3", Toast.LENGTH_SHORT).show();
                    }
                    textViewCampaignCounter.setText((campaignCount - 1) + " of 3 Campaigns Loaded");
                }else {
                    Toast.makeText(CampaignsManagement.this, "Campaigns full, clear first", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void queryCampaigns(){
        animLoading.setVisibility(View.VISIBLE);
        //GET VARS
        campaignElectionID = getElectionID();
        campaignType = spinType.getSelectedItem().toString().trim();
        campaignProvince = spinProvince.getSelectedItem().toString().trim();
        campaignDistrict = spinDistrict.getSelectedItem().toString().trim();
        campaignWard = editWard.getText().toString().trim();


        //Ref
        DatabaseReference campaignsRef = campaignsDB.getReference("Campaigns/" + campaignElectionID);

        //Query
        Query typeQuery = (Query) campaignsRef.orderByChild("type").equalTo(campaignType);

        typeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!arrayAdapterCampaigns.isEmpty()){
                    arrayAdapterCampaigns.clear();
                }
                if (!arrayListCampaignsID.isEmpty()){
                    arrayListCampaignsID.clear();
                }
                if (!arrayListCampaignListings.isEmpty()){
                    arrayListCampaignListings.clear();
                }
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Campaign currentCampaign = snapshot.getValue(Campaign.class);
                        //get Return Data
                        returnType = currentCampaign.getType();
                        returnProvince = currentCampaign.getProvince();
                        returnDistrict = currentCampaign.getDistrict();
                        returnWard = currentCampaign.getWard();

                        //Test Type
                        if (returnType.equals("National Campaign")){
                            //TODO Add National Campaigns
                            arrayAdapterCampaigns.add(currentCampaign);
                            arrayListCampaignsID.add(currentCampaign.getId());
                            arrayListCampaignListings.add(currentCampaign.toString());
                        }else if (returnType.equals("Constituency Campaign")){
                            //TODO Test Province, proceed, else return none
                            //TODO Test District, copy, else return none

                            if (returnProvince.equals(campaignProvince)){

                                if (returnDistrict.equals(campaignDistrict)){
                                    arrayAdapterCampaigns.add(currentCampaign);
                                    arrayListCampaignsID.add(currentCampaign.getId());
                                    arrayListCampaignListings.add(currentCampaign.toString());
                                }else {
                                    animLoading.setVisibility(View.GONE);
                                }
                            }else {
                                animLoading.setVisibility(View.GONE);
                            }

                        }else if (returnType.equals("Ward Level Campaign")){
                            //TODO Test Province, proceed, else return none
                            //TODO Test District, proceed, else return none
                            //TODO Test Ward, copy, else return none

                            if (returnProvince.equals(campaignProvince)){
                                if (returnDistrict.equals(campaignDistrict)){
                                    if (returnWard.equals(campaignWard)){
                                        arrayAdapterCampaigns.add(currentCampaign);
                                        arrayListCampaignsID.add(currentCampaign.getId());
                                        arrayListCampaignListings.add(currentCampaign.toString());
                                    }else {
                                        animLoading.setVisibility(View.GONE);
                                    }
                                }else {
                                    animLoading.setVisibility(View.GONE);
                                }
                            }else {
                                animLoading.setVisibility(View.GONE);
                            }
                        }

                    }
                    if (!arrayListCampaignsID.isEmpty() && search == true){
                        showPopupCampaigns();
                        arrayAdapterCampaigns.notifyDataSetChanged();
                        animLoading.setVisibility(View.GONE);
                    }else {
                        animLoading.setVisibility(View.GONE);
                    }

                }else if (!dataSnapshot.exists()){
                    Toast.makeText(CampaignsManagement.this, "No Campaigns here", Toast.LENGTH_SHORT).show();
                    animLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void registerCampaign(){
        animLoading.setVisibility(View.VISIBLE);
        //TODO make ref, get campaign ID, upload data
        DatabaseReference campaignRef = campaignsDB.getReference("Campaigns/" + campaignElectionID);
        //get Key
        campaignID = campaignRef.push().getKey();

        //Create Campaign
        Campaign newCampaign = new Campaign(campaignElectionID, campaignID, campaignType, campaignProvince,
                campaignDistrict, campaignRegion, campaignWard,
                null, null, null, Boolean.TRUE);

        campaignRef.child(campaignID).setValue(newCampaign)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CampaignsManagement.this, "CampaignRegistered", Toast.LENGTH_SHORT).show();
                            editRegion.setText("");
                            editWard.setText("");
                            animLoading.setVisibility(View.GONE);
                            return;
                        }if (!task.isSuccessful()){
                            Toast.makeText(CampaignsManagement.this, "Error Happened", Toast.LENGTH_SHORT).show();
                            animLoading.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void updateElections(){
        animLoading.setVisibility(View.VISIBLE);
        //Ref
        DatabaseReference electionsRef = electionsDB.getReference("Elections");

        electionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //Clear Lists
                    arrayListElectionsID.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        //Get Elections
                        Election currentElection = snapshot.getValue(Election.class);
                        final String id = currentElection.getId();
                        //Update Array
                        arrayListElectionsID.add(id);
                        arrayAdapterElections.add(currentElection);
                        arrayAdapterElections.notifyDataSetChanged();
                        animLoading.setVisibility(View.GONE);
                    }
                }else if (!dataSnapshot.exists()){
                    //No Elections Registered
                    Toast.makeText(CampaignsManagement.this, "No Elections Available", Toast.LENGTH_LONG).show();
                    Intent toElections = new Intent(CampaignsManagement.this, ElectionsManagement.class);
                    startActivity(toElections);
                    finish();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getElectionID(){
        //animLoading.setVisibility(View.VISIBLE);
        final int index = spinElection.getSelectedItemPosition();
        //getElectionID
        return arrayListElectionsID.get(index);
    }

    public boolean verifySetData(){
        campaignType = spinType.getSelectedItem().toString().trim();
        campaignProvince = spinProvince.getSelectedItem().toString().trim();
        campaignDistrict = spinDistrict.getSelectedItem().toString().trim();
        campaignRegion = editRegion.getText().toString().trim();
        campaignWard = editWard.getText().toString().trim();

        if (campaignType.equals("National Campaign")){
            campaignProvince = "-";
            campaignDistrict = "-";
            campaignRegion = "-";
            campaignWard = null;
        }else if (campaignType.equals("Constituency Campaign")){
            if (TextUtils.isEmpty(campaignRegion)){
                editRegion.setError("InputRegion");
                editRegion.requestFocus();
                return false;
            }
            campaignWard = null;
        }else if (campaignType.equals("Ward Level Campaign")){
            if (TextUtils.isEmpty(campaignRegion)){
                editRegion.setError("InputRegion");
                editRegion.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty((campaignWard))){
                editWard.setError("Input Ward Number");
                editWard.requestFocus();
                return false;
            }

        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v == btnCreate){
            search = false;
            boolean i = verifySetData();

            if (i == true){
                //TODO Get Election ID
                //TODO Create Election
                campaignElectionID = getElectionID();
                registerCampaign();

            }
        }

        if (v == btnSearch){
            search = true;
            queryCampaigns();
        }

        if (v == btnClearRealTime){
            arrayListRunTimeCampaigns.clear();
            campaignCount = 0;
            textViewCampaignCounter.setText(Integer.toString(campaignCount) + " of 3  Campaigns Loaded");
            Toast.makeText(this, "Cleared Lists", Toast.LENGTH_SHORT).show();
        }

        if (v == btnStartRealtime){
            if (camp1Camp.isEmpty() || camp1Election.isEmpty() || camp2Camp.isEmpty() ||
            camp2Election.isEmpty() || camp3Camp.isEmpty() || camp3Election.isEmpty()){
                Toast.makeText(this, "Load more Campaigns", Toast.LENGTH_SHORT).show();
                return;
            }else {
                Intent toSetup = new Intent(CampaignsManagement.this, RuntimeInstructions.class);
                toSetup.putExtra("camp1Camp", camp1Camp);
                toSetup.putExtra("camp1Election", camp1Election);
                toSetup.putExtra("camp2Camp", camp2Camp);
                toSetup.putExtra("camp2Election", camp2Election);
                toSetup.putExtra("camp3Camp", camp3Camp);
                toSetup.putExtra("camp3Election", camp3Election);
                startActivity(toSetup);
                finish();
            }

        }

    }
}
