package com.example.host.atomicclock;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Helper {
    static String curCity;
    private final static ArrayList<City> citiesGeneral = new ArrayList<>();
    private final RetrieveFeedTask retFeedTask = new RetrieveFeedTask();

    static RetrieveFeedTask getRetFeedTask(){
      Helper helper = new Helper();

      return helper.getFeedTask();
    }

    private RetrieveFeedTask getFeedTask(){
        synchronized (this){

        }
        return retFeedTask;
    }

   static public class RetrieveFeedTask extends AsyncTask<Object, Document, Object> {
        private Document doc;
        private String city;
        private ArrayList<?> citiesToCompare;


        final protected Object doInBackground(Object... objs) {
            String url;
            Activity context;

            citiesToCompare = (ArrayList<?>) objs[2];
            context = (Activity) objs[1];
            city = (String)objs[0];
            url = context.getResources().getString(R.string.url);

            Connection con = Jsoup.connect(url+city);
            try {
                doc = con.get();
                return doc;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


    }

    static String getCityFromDoc(Document doc){
        Matcher match;
        Pattern patt;
        String str = doc.select("div#msgdiv").text();
        String city;

        patt = Pattern.compile("^.+,");
        match = patt.matcher(str);
        city = match.find() ? match.group() : "Regexp no matches";

        return city.substring(0, city.length()-1);
    }

    static void updateMainUI(Document doc, MainActivity context, ArrayList<String> city){
        TextView tvC = context.findViewById(R.id.tvMainCity);
        TextView tvT = context.findViewById(R.id.tvMainTime);
        ImageButton addToFavBtn = context.findViewById(R.id.btnToFav);

        for(String c : city){
            if(context.curCity.equals(c)){
                addToFavBtn.setVisibility(View.INVISIBLE);
                addToFavBtn.setEnabled(false);
                break;
            }
        }

        tvT.setText(doc.select("div#twd").text());
        tvC.setText(doc.select("div#msgdiv").text());
    }


    static ArrayList<City> getCities(ArrayList<String> listStr, Activity context){
        Helper helper = new Helper();

        return helper.getAndUpdateCitiesGeneral(listStr, context);
    }

   /* private FutureTask<ArrayList<City>>  execFutureTaskSub(ArrayList<String> listStr, Activity context){
        FutureTask<ArrayList<City>> future =  new FutureTask<>( new RunFeedTask(listStr, context));
        Thread t = new Thread(future);

        t.start();

        return  future;
    }*/

    private ArrayList<City> getAndUpdateCitiesGeneral(List<String> cities, Activity context){
        citiesGeneral.clear();
        ArrayList<Thread> threads = new ArrayList<>();
        synchronized (citiesGeneral){
            for(String c : cities) {
                RunFeedTask runFeedTask = new RunFeedTask(c, context);
                Thread t = new Thread(runFeedTask,String.format("Thread of %s city", c));
                threads.add(t);
                t.setDaemon(true);
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
