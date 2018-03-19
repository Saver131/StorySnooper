package com.example.saverbullets.senior;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * Created by SaverBullets on 12/5/2016.
 */

public class LoginActivity extends AppCompatActivity {
    private Button contButton;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        contButton = (Button) findViewById(R.id.continueBtn);
        contButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
