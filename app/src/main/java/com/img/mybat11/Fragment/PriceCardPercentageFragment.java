package com.img.mybat11.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.img.mybat11.Adapter.PriceCardPercentageAdapter;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.priceCardGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PriceCardPercentageFragment extends Fragment {

    ExpandableHeightListView priceCard;
    ArrayList<priceCardGetSet> list;

    GlobalVariables gv;

    Context context;

    public PriceCardPercentageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_price_card_percentage, container, false);
        context = getActivity();

        gv = (GlobalVariables) context.getApplicationContext();
//        list = gv.getPriceCard();

        priceCard= v.findViewById(R.id.priceCard);
        priceCard.setExpanded(true);
//        priceCard.setAdapter(new PriceCardPercentageAdapter(context,list));

        return v;
    }

}
