package com.example.host.atomicclock;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import org.jsoup.nodes.Document;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;



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
    String curCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView.ItemDecoration itemDecoration;

        arrcitiesStr = new String[] {"Москва", "Симферополь", "Париж"};
        citiesListStr = new ArrayList<>();
        citiesListStr =  new ArrayList<> (Arrays.asList(arrcitiesStr));

        remFrFavBtn = findViewById(R.id.btnFromFav);
        addToFavBtn = findViewById(R.id.btnToFav);
        tvMainTime = findViewById(R.id.tvMainTime);
        tvMainCity = findViewById(R.id.tvMainCity);
        svMain = findViewById(R.id.svMain);
        fab = findViewById(R.id.fab);
        rvMain = findViewById(R.id.rvMain);
        urlsrc = getResources().getString(R.string.url);

        addToFavBtn.setOnClickListener(this);
        fab.setOnClickListener(this);
       //remFrFavBtn.setOnClickListener(this);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        llManager.scrollToPosition(0);
        rvMain.setLayoutManager(llManager);

        //TODO REGEXP на определение назвагния города из данных html страницы
        svMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    doc = (Document) Helper.getRetFeedTask().execute(s, MainActivity.this, citiesListStr).get();
                } catch (Exception e){
                    e.printStackTrace();
                }
                addToFavBtn.setEnabled(true);
                addToFavBtn.setVisibility(View.VISIBLE);
                curCity = Helper.getCityFromDoc(doc);
                Helper.updateMainUI(doc, MainActivity.this, citiesListStr);

                return false;
            }
        });

        try{
           citiesList = Helper.getCities(citiesListStr, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new RecyclerViewClockAdapter(citiesList);
        rvMain.setAdapter(adapter);
        itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        rvMain.addItemDecoration(itemDecoration);
        animation = new AlphaAnimation(1.0f, 0.2f);
        animation.setDuration(1000);
    }

    @Override
    public void onClick(View view){
        Log.d("CLICKED", "HERE");
        switch(view.getId()){

            case R.id.btnFromFav: {
                int position = (Integer)view.getTag();
                if(citiesListStr.get(position).equals(curCity)) {
                    addToFavBtn.setVisibility(View.VISIBLE);
                    addToFavBtn.setEnabled(true);
                }
                citiesListStr.remove(position);
                try {
                    citiesList = Helper.getCities(citiesListStr, MainActivity.this);
                    adapter.updateData(citiesList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.btnToFav: {
                if(tvMainCity.getText() != getResources().getString(R.string.startLabel)) {
                    //addToFavBtn.setImageDrawable(getDrawable(R.drawable.baseline_done_black_18dp));
                    addToFavBtn.startAnimation(animation);
                    addToFavBtn.setVisibility(View.INVISIBLE);
                    addToFavBtn.setEnabled(false);
                    citiesListStr.add(curCity);
                    try {
                       //addToFavBtn.setImageDrawable(getDrawable(R.drawable.baseline_add_black_18dp));
                        citiesList = Helper.getCities(citiesListStr, MainActivity.this);
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
                     doc = (Document) Helper.getRetFeedTask().execute(curCity, MainActivity.this, citiesListStr).get();
                   } catch (Exception e){
                       e.printStackTrace();
                   }
                   Helper.updateMainUI(doc, this, citiesListStr);
                }

                try{
                    citiesList = Helper.getCities(citiesListStr, MainActivity.this);
                    adapter.updateData(citiesList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


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
        public ListItemViewHolder onCreateViewHolder(
                @NonNull ViewGroup viewGroup, int viewType) {
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
