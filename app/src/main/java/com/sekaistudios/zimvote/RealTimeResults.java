package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RealTimeResults extends AppCompatActivity{
    //Dec Variables
    private ListView lCampaign;
    private LottieAnimationView animLoading;

    //Dec Adapters
    private ArrayList<Candidate> arrayListCandidates = new ArrayList<>();
    private ArrayList<String> arrayListCandidatesID = new ArrayList<>();
    private ArrayList<Integer> arrayListCandidateVotes = new ArrayList<>();
    customResultsAdapter candidatesAdapter;

    //Firebase
    private FirebaseDatabase candidatesDB, votesDB;

    //Storage
    String electionID, campaignID;
    private int candidateCount = 0;
    private int imagesLoaded = 0;

    private static int SPLASH_TIME_OUT = 10000;

    private int voteCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_results);
        //Get Bundle
        Bundle bundle = getIntent().getExtras();
        electionID = bundle.getString("electionID");
        campaignID = bundle.getString("campaignID");

        //Bind Values
        lCampaign = (ListView)findViewById(R.id.listCandidates);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);

        //FirebaseInit
        candidatesDB = FirebaseDatabase.getInstance();
        votesDB = FirebaseDatabase.getInstance();

        getCandidates();
        //TODO Populate Candidate Lists with first election
        candidatesAdapter = new customResultsAdapter();
        lCampaign.setAdapter(candidatesAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide Assets
                notifyDataset();
                animLoading.setVisibility(View.GONE);
            }
        }, SPLASH_TIME_OUT);



    }

    class customResultsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return candidateCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view  = getLayoutInflater().inflate(R.layout.cardview_candidate_result ,null);

            final ImageView imgCandidate = (ImageView)view.findViewById(R.id.imageViewCandidateImage);
            TextView txtCandidateName = (TextView) view.findViewById(R.id.textViewCandidateName);
            TextView txtCandidateParty = (TextView) view.findViewById(R.id.textViewCandidateParty);
            TextView txtCandidateVoteCount = (TextView) view.findViewById(R.id.textViewCandidateVoteCount);


            final Candidate currentCandidate = arrayListCandidates.get(position);
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("CandidateImages/"
                    + currentCandidate.getCampaignID() + "/" + currentCandidate.getId() + ".jpg");

            if (imagesLoaded < candidateCount){
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(RealTimeResults.this).load(uri).into(imgCandidate);
                        imagesLoaded = imagesLoaded + 1;
                    }
                });
            }

            txtCandidateName.setText(currentCandidate.getName());
            txtCandidateParty.setText(currentCandidate.getParty());
            txtCandidateVoteCount.setText(String.valueOf(currentCandidate.getVoteCount()));
            return view;
        }
    }

    public void getCandidates(){
        animLoading.setVisibility(View.VISIBLE);
        if (!arrayListCandidates.isEmpty()){
            arrayListCandidates.clear();
        }
        if (!arrayListCandidatesID.isEmpty()){
            arrayListCandidatesID.clear();
            candidateCount = 0;
        }
        if (!arrayListCandidateVotes.isEmpty()){
            arrayListCandidateVotes.clear();
        }

        //TODO Get Ref,
        DatabaseReference candidatesRef = candidatesDB.getReference("Results/" + electionID + "/" + campaignID );
        candidatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Candidate currentCandidate = snapshot.getValue(Candidate.class);
                    arrayListCandidates.add(currentCandidate);
                    arrayListCandidatesID.add(currentCandidate.getId());
                    candidateCount = candidateCount + 1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void notifyDataset(){
        candidatesAdapter.notifyDataSetChanged();
    }

}

