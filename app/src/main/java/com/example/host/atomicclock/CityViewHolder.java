package com.example.host.atomicclock;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

class CityViewHolder {
    TextView tvMainTime;
    TextView tvMainCity;
    ImageButton addToFavBtn;
    ImageButton remFrFavBtn;
    SearchView svMain;
    FloatingActionButton fab;
    RecyclerView rvMain;

    CityViewHolder (MainActivity activity){
        remFrFavBtn = activity.findViewById(R.id.btnFromFav);
        addToFavBtn = activity.findViewById(R.id.btnToFav);
        tvMainTime = activity.findViewById(R.id.tvMainTime);
        tvMainCity = activity.findViewById(R.id.tvMainCity);
        svMain = activity.findViewById(R.id.svMain);
        fab = activity.findViewById(R.id.fab);
        rvMain = activity.findViewById(R.id.rvMain);
    }
}
