package com.example.host.atomicclock;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    TextView tvMainTime;
    SearchView svMain;
    TextView svMainCity;
    String urlsrc;
    String resStr;
    FloatingActionButton fab;
    String curCity;
    RecyclerView rvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMainTime = findViewById(R.id.tvMainTime);
        svMainCity = findViewById(R.id.tvMainCity);
        svMain = findViewById(R.id.svMain);
        urlsrc = getResources().getString(R.string.url);
        fab = findViewById(R.id.fab);
        rvMain = findViewById(R.id.rvMain);

        LinearLayoutManager llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        llManager.scrollToPosition(0);
        rvMain.setLayoutManager(llManager);

        ArrayList<String> data = new ArrayList<String>();
        data.add("New York");
        data.add("Canberra");
        data.add("Moskow");

        RecyclerViewClockAdapter adapter = new RecyclerViewClockAdapter(data);
        rvMain.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        rvMain.addItemDecoration(itemDecoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.getRetFeedTask().execute(curCity, MainActivity.this);
            }
        });

        svMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                curCity = s;
                Helper.getRetFeedTask().execute(curCity, MainActivity.this);
                return false;
            }
        });
    }

    public class RecyclerViewClockAdapter extends RecyclerView.Adapter<RecyclerViewClockAdapter.ListItemViewHolder>{
        private ArrayList<String> cities;

        RecyclerViewClockAdapter(ArrayList<String> modelData){
            if (modelData == null) {
                throw new IllegalArgumentException(
                        "modelData must not be null");
            }
            this.cities = modelData;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.layout_tv_city,
                            viewGroup,
                            false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                ListItemViewHolder viewHolder, int position) {
            String city = cities.get(position);
            viewHolder.city.setText(city);
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        public class ListItemViewHolder extends RecyclerView.ViewHolder{
            TextView city;
            public ListItemViewHolder(View itemView){
                super(itemView);
                city = itemView.findViewById(R.id.layout_tv_city);
            }
        }

    }
}
