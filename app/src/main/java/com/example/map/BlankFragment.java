package com.example.map;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Location;
import com.here.sdk.core.errors.InstantiationErrorException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    Math m;

    public TextView textView;

    public  View rootview;

    public MainActivity mainActivity = new MainActivity();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int i=0;



    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int i=0;
         rootview = inflater.inflate(R.layout.fragment_blank, container, false);
         //setTextView(rootview);

        return rootview;

    }

    public void setTextView(View view, Location location) {

        textView = view.findViewById(R.id.blankfrag_textView);


            try {

                System.out.println("im getting a new coordinate at lat:"+ mainActivity.coordinates.get(0).latitude+"long :" +mainActivity.coordinates.get(0).longitude);

                    Location mylocation= mainActivity.Getlocation(0);
                    // System.out.println("my location is :"+mylocation.coordinates.latitude+"\n"+mylocation.coordinates.longitude);
                    Location endlocation=mainActivity.Getlocation(1);
                    double distance = calculateDistance(mylocation, endlocation);

              String str=String.format("%.2f",(distance));
                    textView.setText(str+"Km");


                System.out.println("im in the wierd try catch");

            } catch (NullPointerException exception ){

            }


    }


    private double calculateDistance( Location s, Location e){
        Double lat_s=s.coordinates.latitude;
        Double long_s=s.coordinates.longitude;
        Double lat_e=e.coordinates.latitude;
        Double long_e=e.coordinates.longitude;


        double Distance =m.acos(m.sin(lat_s)*m.sin(lat_e)+m.cos(lat_s)*m.cos(lat_e)*m.cos(long_s-long_e))*6371;

        return Distance;
    }
}
