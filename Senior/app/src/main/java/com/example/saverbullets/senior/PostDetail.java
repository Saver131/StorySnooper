package com.example.saverbullets.senior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by SaverBullets on 1/18/2017.
 */

public class PostDetail extends AppCompatActivity{

    private String veracity;
    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        Bundle bundle = getIntent().getExtras();
        veracity = bundle.getString("veracity");;
        id = bundle.getString("id");
        TextView veracityTxt = (TextView)findViewById(R.id.VeracityDetail);
        TextView veracityReason = (TextView)findViewById(R.id.VeracityReason);
        veracityTxt.setText("Truthness\n"+veracity);
        System.out.println(id);

        switch(id){
            case "141108613290":
                    veracityReason.setText("Reasons\nTrustworthy Source\nAgreed Comment\nTrustable Link");
                break;
            case "146406732438":
                veracityReason.setText("Reasons\nMost Trusted Source\nMostly Agreed Comment\nTrustable Link");
                break;
            case "129558990394402":
                veracityReason.setText("Reasons\nMost Trusted Source\nMostly Agreed Comment\nTrustable Link");
                break;
            case "136045649773277":
                veracityReason.setText("Reasons\nTrustworthy Source\nMostly Agreed Comment\nTrustable Link");
                break;
            case "159005400803388":
                veracityReason.setText("Reasons\nTrustworthy Source\nMostly Agreed Comment\nTrustable Link");
                break;
            case "221317851322226":
                veracityReason.setText("Reasons\nDoubtful Source\nAgreed Comment\nUntrustable Link");
                break;
            default:
                break;
        }
    }
}
