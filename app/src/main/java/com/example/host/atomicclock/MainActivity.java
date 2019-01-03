package com.example.host.atomicclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.google.gson.internal.bind.TypeAdapters.URL;

public class MainActivity extends AppCompatActivity  {
    TextView tvMain;
    SearchView svMain;
    String urlsrc;
    String resStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMain = findViewById(R.id.tvMain);
        svMain = findViewById(R.id.svMain);
        urlsrc = "https://time.is/";

        svMain.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                String url = urlsrc + s;
                Helper.getRetFeedTask().execute(url, MainActivity.this);
                return false;
            }
        });
    }
}
