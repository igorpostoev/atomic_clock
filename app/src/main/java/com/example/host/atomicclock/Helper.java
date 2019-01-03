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

    private RetrieveFeedTask getRetFeedTaskInternal(){
        return new RetrieveFeedTask();
    }
    public class RetrieveFeedTask extends AsyncTask<Object,Document, Document> {
        private Exception exception;
        private Activity context;
        private Document doc;
        protected Document doInBackground(Object... objs) {
            String url = (String) objs[0];
            context = (Activity) objs[1];
            Connection con = Jsoup.connect(url);
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
            TextView tv = context.findViewById(R.id.tvMain);
            tv.setText(doc[0].select("div#twd").text());
        }

        protected void onPostExecute(Document feed) {

        }
    }


}
