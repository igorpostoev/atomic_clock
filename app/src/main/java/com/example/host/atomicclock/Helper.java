package com.example.host.atomicclock;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class Helper {

    static Call getClientGetReq(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request);
    }

    static RetrieveFeedTask getRetFeedTask(){
      Helper helper = new Helper();
      return helper.getRetFeedTaskInternal();
    }

    //TODO: адаптированть так, чтобы данные возвращались в главный поток в качестве переменной. возможно опредедление глобальной переменной в хелпере

    private RetrieveFeedTask getRetFeedTaskInternal(){
        return new RetrieveFeedTask();
    }
    public class RetrieveFeedTask extends AsyncTask<Object,Document, Document> {
        private Exception exception;
        private Activity context;
        private Document doc;
        private String city;

        protected Document doInBackground(Object... objs) {
            String url;
            context = (Activity) objs[1];
            city = (String)objs[0];
            url = context.getResources().getString(R.string.url);

            Connection con = Jsoup.connect(url+city);
            try {
                doc = con.get();
                publishProgress(doc);
                return doc;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Document... doc) {
            super.onProgressUpdate();
            TextView tvC = context.findViewById(R.id.tvMainCity);
            TextView tvT = context.findViewById(R.id.tvMainTime);
            tvT.setText(doc[0].select("div#twd").text());
            tvC.setText(city);
        }

        protected void onPostExecute(Document feed) {

        }
    }


}
