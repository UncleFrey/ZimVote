package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.le.AdvertiseData;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PartyManagement extends AppCompatActivity implements View.OnClickListener{
    //Dec Values
    private EditText editName;
    private Button btnCreate;
    private ListView listParties;
    private LottieAnimationView animLoading;

    //Firebase
    FirebaseDatabase partyDB;

    //final Vars
    private String name, id;

    //TODO PArties Adapter
    private ArrayAdapter<Party> arrayAdapterParty;
    private ArrayList<String > arrayListPartyID = new ArrayList<>();

    //TODO Tag Vars
    boolean refreshTag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_management);

        //Bind Vars
        editName = (EditText)findViewById(R.id.editTextPartyName);
        btnCreate = (Button) findViewById(R.id.buttonCreateParty);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);
        listParties = (ListView)findViewById(R.id.listParties);

        //Firenase Init
        partyDB = FirebaseDatabase.getInstance();

        //TODO PArties List
        arrayAdapterParty = new ArrayAdapter<Party>(this, android.R.layout.simple_list_item_1);
        listParties.setAdapter(arrayAdapterParty);
        updateParties();
        //OnClick
        btnCreate.setOnClickListener(this);

        listParties.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //get slected party id
                final String partyid = arrayListPartyID.get(position);
                //Ref
                DatabaseReference partyRef = partyDB.getReference("Parties/" + partyid);
                partyRef.removeValue();
                refreshTag = true;
                updateParties();
                Toast.makeText(PartyManagement.this, "Deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void updateParties(){
        if ((!arrayListPartyID.isEmpty()) && refreshTag == true){
            arrayListPartyID.clear();
            arrayAdapterParty.clear();
        }

        animLoading.setVisibility(View.VISIBLE);
        DatabaseReference partyRef = partyDB.getReference("Parties");
        partyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Party currentParty = snapshot.getValue(Party.class);
                        arrayAdapterParty.add(currentParty);
                        arrayListPartyID.add(currentParty.getId());
                    }
                }else {
                    Toast.makeText(PartyManagement.this, "No Parties Available.", Toast.LENGTH_SHORT).show();
                    animLoading.setVisibility(View.GONE);
                    return;
                }
                arrayAdapterParty.notifyDataSetChanged();
                animLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void registerParty(){
        //Verify Vars
        String electionName =editName.getText().toString().trim();;
        if (electionName.isEmpty()){
            editName.setError("Enter Name");
            editName.requestFocus();
            return;
        }

        animLoading.setVisibility(View.VISIBLE);
        if (electionName.equals("secret")){
            name = "Independent";
        }else{
            name = electionName;
        }

        //Make  Ref
        DatabaseReference partyRef = partyDB.getReference("Parties");

        id = partyRef.push().getKey();


        //Make class
        Party newParty = new Party(id, name);


        partyRef.child(id).setValue(newParty).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PartyManagement.this, "Party Added", Toast.LENGTH_SHORT).show();
                    refreshTag = true;
                    updateParties();
                    animLoading.setVisibility(View.GONE);
                    editName.setText("");
                }else if (!task.isSuccessful()){
                    Toast.makeText(PartyManagement.this, "Error,try again", Toast.LENGTH_SHORT).show();
                    animLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnCreate){
            registerParty();
        }

    }
}
