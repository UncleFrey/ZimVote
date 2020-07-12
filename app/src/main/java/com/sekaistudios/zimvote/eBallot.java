package com.sekaistudios.zimvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class eBallot extends AppCompatActivity implements View.OnClickListener{
    //Dec Views
    private TextView txtCandidatePosition, txtCastCount;
    private ListView listCandidates;
    private LottieAnimationView animLoading;

    //voter
    private String voterid, votername;
    private String camp1Election, camp1Camp;
    private String camp2Election, camp2Camp;
    private String camp3Election, camp3Camp;

    //TODO Runtime Lists
    private ArrayList<Candidate> arrayListCandidates = new ArrayList<>();
    private ArrayList<String> arrayListCandidatesID = new ArrayList<>();

    //TODO Runtime Vars
    private boolean press = false;
    private boolean voting = true;

    //TODO Flags
    int currentCampaign = 1;
    int candidateCount = 0;

    customCandidateAdapter candidateAdapter;

    private static int SPLASH_TIME_OUT = 10000;
    private int votePress = 0;
    private int votePressPos = 0;


    //TODO Image Population
    private ArrayList<String> currentPathList = new ArrayList<>();

    //TODO Firebase
    private FirebaseDatabase candidateDB;

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Make your Vote", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_ballot);

        //Bind Views
        txtCandidatePosition = (TextView)findViewById(R.id.textViewCandidateCampaign);
        txtCastCount = (TextView)findViewById(R.id.textViewVotesCastCount);
        listCandidates = (ListView)findViewById(R.id.listViewCurrentCandidates);
        animLoading = (LottieAnimationView)findViewById(R.id.animationViewLoading);

        //Get VAlues
        Bundle bundle = getIntent().getExtras();
        camp1Election = bundle.getString("camp1Election");
        camp1Camp = bundle.getString("camp1Camp");
        camp2Election = bundle.getString("camp2Election");
        camp2Camp = bundle.getString("camp2Camp");
        camp3Election = bundle.getString("camp3Election");
        camp3Camp = bundle.getString("camp3Camp");
        voterid = bundle.getString("voterID");
        votername = bundle.getString("voterName");

        //TODO Greet Voter
        Toast.makeText(eBallot.this, "Welcome " + votername,
                Toast.LENGTH_SHORT).show();

        getCandidateData();

        //TODO Populate Candidate Lists with first election
        candidateAdapter = new customCandidateAdapter();
        listCandidates.setAdapter(candidateAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animLoading.setVisibility(View.GONE);
            }
        }, SPLASH_TIME_OUT);
    }

    class customCandidateAdapter extends BaseAdapter{

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
            view  = getLayoutInflater().inflate(R.layout.cardview_candidate_ballot_listing ,null);

            final ImageView imgCandidate = (ImageView)view.findViewById(R.id.imageViewCandidateImage);
            TextView txtCandidateName = (TextView) view.findViewById(R.id.textViewCandidateName);
            TextView txtCandidateParty = (TextView) view.findViewById(R.id.textViewCandidateParty);
            Button btnVote = (Button)view.findViewById(R.id.buttonVote);


            final Candidate currentCandidate = arrayListCandidates.get(position);
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("CandidateImages/"
            + currentCandidate.getCampaignID() + "/" + currentCandidate.getId() + ".jpg");

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (voting == true){
                        Glide.with(eBallot.this).load(uri).into(imgCandidate);
                    }else {
                        return;
                    }

                }
            });


            txtCandidateName.setText(currentCandidate.getName());
            txtCandidateParty.setText(currentCandidate.getParty());

            btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Cast vote
                    if (votePress == 0){ ;
                        votePress = votePress + 1;
                        votePressPos = position;
                        Toast.makeText(eBallot.this, "Press Vote Again to confirm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (votePress == 1 && votePressPos == position){
                        if (!currentPathList.isEmpty()){
                            currentPathList.clear();
                        }
                        votePress = 0;
                        press = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Hide Assets
                                animLoading.setVisibility(View.GONE);
                            }
                        }, SPLASH_TIME_OUT);

                    }else if (votePress == 1 && votePressPos !=position){
                        votePress = 0;
                        Toast.makeText(eBallot.this, "Place Vote on one Candidate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
            return view;
        }
    }

    public void getCandidateData(){
        //TODO Clear Candidates
        //TODO Clear Get Candidates
        //TODO Update Flags

        //Clear Cand
        if (!arrayListCandidatesID.isEmpty()){
            arrayListCandidatesID.clear();
            arrayListCandidates.clear();
        }

        //Ref


    }

    @Override
    public void onClick(View v) {

    }
}
