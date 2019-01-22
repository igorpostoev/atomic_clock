package com.example.host.atomicclock;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.host.atomicclock.Helper.getCities;
import static com.example.host.atomicclock.Helper.getCityFromDoc;
import static com.example.host.atomicclock.Helper.getRetFeedTask;
import static com.example.host.atomicclock.Helper.updateMainUI;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvMainTime;
    SearchView svMain;
    TextView tvMainCity;
    String urlsrc;
    FloatingActionButton fab;
    RecyclerView rvMain;
    String[] arrcitiesStr;
    ArrayList<String> citiesListStr;
    ArrayList<City> citiesList;
    RecyclerViewClockAdapter adapter;
    ImageButton addToFavBtn;
    ImageButton remFrFavBtn;
    AlphaAnimation animation;
    Document doc;
    Connection.Response res;
    String curCity;
    CityViewHolder cvh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView.ItemDecoration itemDecoration;
        //Initial list of cities, can be empty.
        arrcitiesStr = new String[] {"Москва", "Симферополь", "Париж"};
        citiesListStr =  new ArrayList<> (Arrays.asList(arrcitiesStr));
        //Find view
        cvh = new CityViewHolder(this);

        remFrFavBtn = cvh.remFrFavBtn;
        addToFavBtn = cvh.addToFavBtn;
        tvMainTime = cvh. tvMainTime;
        tvMainCity = cvh.tvMainCity;
        svMain = cvh.svMain;
        fab = cvh.fab;
        rvMain = cvh.rvMain;

        //Source url for time check
        urlsrc = getResources().getString(R.string.url);

        addToFavBtn.setOnClickListener(this);
        fab.setOnClickListener(this);

        //Managing recycle view
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        llManager.scrollToPosition(0);
        rvMain.setLayoutManager(llManager);

        //Initial List initialisation
        try{
           citiesList = getCities(citiesListStr, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //RV working
        adapter = new RecyclerViewClockAdapter(citiesList);
        rvMain.setAdapter(adapter);
        itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        rvMain.addItemDecoration(itemDecoration);
        animation = new AlphaAnimation(1.0f, 0.2f);
        animation.setDuration(1000);

        // Set listener for SearchView
        svMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                int status;
                try {
                    res = (Connection.Response) getRetFeedTask().execute(s, MainActivity.this).get();
                    if(res!=null){
                        doc = res.parse();
                        curCity = getCityFromDoc(doc);
                        updateMainUI(doc, cvh, citiesListStr, curCity);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    //Method maintains listener body for different views
    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.btnFromFav: {
                int position = (Integer)view.getTag();
                if(citiesListStr.get(position).equals(curCity)) {
                    addToFavBtn.setVisibility(View.VISIBLE);
                    addToFavBtn.setEnabled(true);
                }
                citiesListStr.remove(position);
                try {
                    citiesList = getCities(citiesListStr, MainActivity.this);
                    adapter.updateData(citiesList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.btnToFav: {
                if(tvMainCity.getText() != getResources().getString(R.string.startLabel)) {
                    addToFavBtn.startAnimation(animation);
                    addToFavBtn.setVisibility(View.INVISIBLE);
                    addToFavBtn.setEnabled(false);
                    citiesListStr.add(curCity);
                    try {
                        citiesList = getCities(citiesListStr, MainActivity.this);
                        adapter.updateData(citiesList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.fab: {

                if(tvMainCity.getText() != getResources().getString(R.string.startLabel)){
                   try{
                     doc = (Document) getRetFeedTask().execute(curCity, MainActivity.this, citiesListStr).get();
                   } catch (Exception e){
                       e.printStackTrace();
                   }
                  updateMainUI(doc, cvh, citiesListStr, curCity);
                }

                try{
                    citiesList = getCities(citiesListStr, MainActivity.this);
                    adapter.updateData(citiesList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //complex class for handling recycle view
    public class RecyclerViewClockAdapter extends RecyclerView.Adapter<RecyclerViewClockAdapter.ListItemViewHolder>{
        private List<City> cities;

        RecyclerViewClockAdapter(ArrayList<City> modelData){
            updateData(modelData);
        }

        void updateData(List<City> modelData){
            if (modelData == null) {
                throw new IllegalArgumentException(
                        "modelData must not be null");
            }
            this.cities = modelData;
            notifyDataSetChanged();
        }

        @Override
        @NonNull
        public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.layout_tv_city,
                            viewGroup,
                            false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                @NonNull ListItemViewHolder viewHolder, int position) {
            City city = cities.get(position);
            viewHolder.name.setText(city.name);
            viewHolder.time.setText(city.time);
            viewHolder.remove.setTag(position);
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        class ListItemViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            TextView time;
            ImageButton remove;
            ListItemViewHolder(View itemView){
                super(itemView);
                name = itemView.findViewById(R.id.layout_tv_city);
                time = itemView.findViewById(R.id.layout_tv_time);
                remove = itemView.findViewById(R.id.btnFromFav);
                remove.setOnClickListener(MainActivity.this);
            }
        }
    }
}
