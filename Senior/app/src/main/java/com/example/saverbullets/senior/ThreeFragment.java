package com.example.saverbullets.senior;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThreeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThreeFragment extends Fragment {
    public ThreeFragment() {
        // Required empty public constructor
    }

    Button button;
    EditText editText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three,
                container, false);
        // Inflate the layout for this fragment

        return view;
    }
    public void onStart() {
        super.onStart();

        button = (Button) getView().findViewById(R.id.check);
        editText = (EditText) getView().findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String url = editText.getText().toString();
                Intent intent = new Intent(getActivity(), VeracityResult.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }

        });

    }
}
