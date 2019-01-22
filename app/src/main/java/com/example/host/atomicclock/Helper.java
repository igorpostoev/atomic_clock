package com.example.host.atomicclock;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Class for different independent actions
class Helper {
    //List for concurrent calculations, general space for threads
    private final static ArrayList<City> citiesGeneral = new ArrayList<>();
    private final static Helper helper = new Helper();

    //methods for creating thread searching for single city
    static RetrieveFeedTask getRetFeedTask(){
        return helper.getFeedTask();
    }

    private RetrieveFeedTask getFeedTask(){
        return new RetrieveFeedTask();
    }

   static public class RetrieveFeedTask extends AsyncTask<Object, Document, Object> {
        private Document doc;
        private String city;

        final protected Object doInBackground(Object... objs) {
            String url;
            MainActivity context;
            Connection.Response response = null;
            context = (MainActivity) objs[1];
            city = (String)objs[0];
            url = context.getResources().getString(R.string.url);

            try {
                try {
                    response = Jsoup.connect(url + city).execute();
                } catch (HttpStatusException e){
                    Snackbar.make(context.tvMainCity, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static String getCityFromDoc(Document doc){
        Matcher match;
        Pattern patt;
        String str;
        String city;

        str = doc.select("div#msgdiv").text();
        patt = Pattern.compile("^.+,");
        match = patt.matcher(str);
        city = match.find() ? match.group() : "Regexp no matches";

        return city.substring(0, city.length()-1);
    }

    //update TextViews on main Layout and add button
    static void updateMainUI(Document doc, CityViewHolder cvh, ArrayList<String> city, String curCity){
        TextView tvC = cvh.tvMainCity;
        TextView tvT = cvh.tvMainTime;
        ImageButton addToFavBtn = cvh.addToFavBtn;

        addToFavBtn.setEnabled(true);
        addToFavBtn.setVisibility(View.VISIBLE);

        for(String c : city){
            if(curCity.equals(c)){
                addToFavBtn.setVisibility(View.INVISIBLE);
                addToFavBtn.setEnabled(false);
                break;
            }
        }

        tvT.setText(doc.select("div#twd").text());
        tvC.setText(doc.select("div#msgdiv").text());
    }

    // start concurrent threads referring to each city to find and return list of cities
    static ArrayList<City> getCities(ArrayList<String> listStr, Activity context){

        return helper.getAndUpdateCitiesGeneral(listStr, context);
    }


    private ArrayList<City> getAndUpdateCitiesGeneral(List<String> cities, Activity context){
        citiesGeneral.clear();
        ArrayList<Thread> threads = new ArrayList<>();

        synchronized (citiesGeneral){
            for(String c : cities) {
                RunFeedTask runFeedTask = new RunFeedTask(c, context);
                Thread t = new Thread(runFeedTask, String.format("Thread of %s city", c));
                t.setDaemon(true);
                threads.add(t);
                t.start();
            }
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        for(int i = 0; i < citiesGeneral.size(); i++ ){
            if(!citiesGeneral.get(i).name.equals(cities.get(i))){
                for(int k = i+1; k < citiesGeneral.size(); k++ ) {
                    if (citiesGeneral.get(k).name.equals(cities.get(i))) {
                        City c = citiesGeneral.get(i);
                        citiesGeneral.set(i, citiesGeneral.get(k));
                        citiesGeneral.set(k, c);
                    }
                }
            }
        }

        return citiesGeneral;
    }

    public class RunFeedTask implements Runnable {
        private Activity context;
        private Document doc;
        private String city;

        RunFeedTask(String city ,Activity context){
            this.city = city;
            this.context = context;
        }

        @Override
        public void run(){
            String url;
            url = context.getResources().getString(R.string.url);
                Connection con = Jsoup.connect(url + city);
                try {
                    doc = con.get();
                } catch (Exception e){
                    e.printStackTrace();
                }

            synchronized (citiesGeneral){
                citiesGeneral.add(new City(city, doc.select("div#twd").text()));
            }
        }
    }
}
