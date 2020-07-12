package com.sekaistudios.zimvote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RuntimeInstructions extends AppCompatActivity implements View.OnClickListener{
    private Button btnStartRuntime;

    //camp1
    private String camp1Election, camp1Camp;
    //camp2
    private String camp2Election, camp2Camp;
    //camp3
    private String camp3Election, camp3Camp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime_instructions);
        //Get Values
        Bundle bundle = getIntent().getExtras();
        camp1Election = bundle.getString("camp1Election");
        camp1Camp = bundle.getString("camp1Camp");
        camp2Election = bundle.getString("camp2Election");
        camp2Camp = bundle.getString("camp2Camp");
        camp3Election = bundle.getString("camp3Election");
        camp3Camp = bundle.getString("camp3Camp");

        //Bind Values
        btnStartRuntime = (Button)findViewById(R.id.buttonVoterRegConfirmation);

        btnStartRuntime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnStartRuntime){
            //Goto Voter Validation
            Intent toValid = new Intent(RuntimeInstructions.this, VoterValidation.class);
            toValid.putExtra("camp1Camp", camp1Camp);
            toValid.putExtra("camp1Election", camp1Election);
            toValid.putExtra("camp2Camp", camp2Camp);
            toValid.putExtra("camp2Election", camp2Election);
            toValid.putExtra("camp3Camp", camp3Camp);
            toValid.putExtra("camp3Election", camp3Election);
            startActivity(toValid);

        }

    }
}
