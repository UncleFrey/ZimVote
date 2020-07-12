package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ElectionsManagement extends AppCompatActivity implements View.OnClickListener{
    //Declare Views
    private EditText editElectionName;
    private Button btnCreate;
    private ListView listElections;
    private LottieAnimationView animLoading;

    //Final Vars
    private String electionID, electionName, electionCampaigns = null, electionLog = null;
    private Boolean electionStatus = Boolean.TRUE;

    //Firebase
    private FirebaseDatabase electionDB;
    private FirebaseDatabase electionDelDB;

    //Voter PopUp
    Dialog dialogExists;

    private ArrayAdapter<Election> arrayAdapterElections;
    private ArrayList<String> arrayListElectionsID = new ArrayList<>();

    //Data management values
    private boolean refreshElections = false;
    private boolean createdElection = false;
    private boolean deleteElection = false;
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elections_management);
        //Bind Views
        editElectionName  = (EditText) findViewById(R.id.editTextElectionName);
        btnCreate = (Button) findViewById(R.id.buttonCreate);
        listElections = (ListView)findViewById(R.id.listViewElections);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);


        //Firebase
        electionDB = FirebaseDatabase.getInstance();
        electionDelDB = FirebaseDatabase.getInstance();

        //List Adapter
        arrayAdapterElections = new ArrayAdapter<Election>(this, android.R.layout.simple_list_item_1);
        listElections.setAdapter(arrayAdapterElections);
        updateElections();

        //Set Onclick
        btnCreate.setOnClickListener(this);

        //Del Refs
        final DatabaseReference electionsRef = electionDelDB.getReference("Elections");
        final DatabaseReference campaignsRef = electionDelDB.getReference("Campaigns");
        final DatabaseReference candidatesRef = electionDelDB.getReference("Candidates");

        listElections.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                animLoading.setVisibility(View.VISIBLE);
                //TODO Delete id nodes @ , Elections, Campaigns, Candidates
                //Ref - Election Node
                final String electionid = arrayListElectionsID.get(position);

                //DeleteElection
                electionsRef.child(electionid).removeValue();
                campaignsRef.child(electionid).removeValue();
                candidatesRef.child(electionid).removeValue();
                deleteElection = true;
                refreshElections = true;
                createdElection = false;
                updateElections();
                Toast.makeText(ElectionsManagement.this, "Election deleted", Toast.LENGTH_SHORT).show();
                animLoading.setVisibility(View.GONE);
                return true;
            }
        });
    }

    public void updateElections(){
        animLoading.setVisibility(View.VISIBLE);
        if (!arrayListElectionsID.isEmpty() && (refreshElections = true)){
            arrayAdapterElections.clear();
            arrayListElectionsID.clear();
        }
        //Ref
        final DatabaseReference electionsRef = electionDB.getReference("Elections");
        electionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Election currentElection = snapshot.getValue(Election.class);
                        arrayAdapterElections.add(currentElection);
                        arrayListElectionsID.add(currentElection.getId());
                    }
                }else if (!dataSnapshot.exists()){
                    Toast.makeText(ElectionsManagement.this, "No Elections Available.", Toast.LENGTH_SHORT).show();
                    animLoading.setVisibility(View.GONE);
                    electionsRef.removeEventListener(this);
                    return;
                }

                refreshElections = true;
                arrayAdapterElections.notifyDataSetChanged();
                animLoading.setVisibility(View.GONE);
                electionsRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showElectionExists(){
        //Make Vars
        TextView txtHead;
        Button btnYes, btnNo;

        dialogExists = new Dialog(this);
        //SetView
        dialogExists.setContentView(R.layout.dialog_yes_no);

        //Bind Vars
        txtHead = (TextView) dialogExists.findViewById(R.id.textViewHeading);
        btnYes = (Button) dialogExists.findViewById(R.id.buttonYes);
        btnNo = (Button) dialogExists.findViewById(R.id.buttonNo);

        //Set Data
        txtHead.setText("Election Exits, View?");

        //Start Dialog
        dialogExists.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogExists.show();
        animLoading.setVisibility(View.GONE);


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO View Election
                Toast.makeText(ElectionsManagement.this, "Viewing...", Toast.LENGTH_SHORT).show();
                Intent toCampaigns = new Intent(ElectionsManagement.this, CampaignsManagement.class);
                dialogExists.dismiss();
                startActivity(toCampaigns);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExists.dismiss();
            }
        });

    }

    public void createElection(){
        createdElection = true;
        //Make Ref
        DatabaseReference electionsRef = electionDB.getReference("Elections");

        //Push election id
        electionID = electionsRef.push().getKey();

        //Create Object
        Election newElection  = new Election(electionID, electionName, electionCampaigns, electionLog, electionStatus);

        electionsRef.child(electionID).setValue(newElection)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ElectionsManagement.this, "Election Created", Toast.LENGTH_SHORT).show();
                            editElectionName.setText("");
                            refreshElections = true;
                            animLoading.setVisibility(View.GONE);
                            updateElections();
                        }
                        return;
                    }
                });
    }

    public void validate(){
        electionName = editElectionName.getText().toString().trim();
        //Validate

        if (TextUtils.isEmpty(electionName)){
            editElectionName.setError("Enter Election Name");
            editElectionName.requestFocus();
            animLoading.setVisibility(View.GONE);
            return;
        }

        //Ref
        DatabaseReference electionsRef = electionDB.getReference("Elections");

        //Query Name
        Query electionsQuery = (Query) electionsRef.orderByChild("name").equalTo(electionName);

        electionsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && createdElection == false && deleteElection == false){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Election currentElection = snapshot.getValue(Election.class);
                        if (electionName.equals(currentElection.getName())){
                            editElectionName.setError("Change This Name");
                            editElectionName.requestFocus();
                            showElectionExists();
                            animLoading.setVisibility(View.GONE);
                        }
                    }

                }else if (!dataSnapshot.exists() && deleteElection == false){
                    createElection();
                    return;

                }
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreate){
            animLoading.setVisibility(View.VISIBLE);
            deleteElection = false;
            validate();
        }

    }
}
