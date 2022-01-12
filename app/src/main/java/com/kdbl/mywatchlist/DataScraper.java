package com.kdbl.mywatchlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataScraper implements Executor {

    public static final int NUM_DATA = 8;
    private final String mURL;

    public DataScraper(String URL) {
        mURL = URL;
    }

    public void populateAnimeInfo(View urlDisplayView) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                String[] data = new String[NUM_DATA];
                try {
                    Document document = Jsoup.connect(mURL).get();
                    Elements animeInfo = document.select("div.spaceit_pad");
                    Iterator<Element> infoIterator = animeInfo.iterator();
                    int i = 0;
                    while(infoIterator.hasNext() && i < NUM_DATA) {
                        String temp = infoIterator.next().text();
                        data[i] = temp.split(":")[1].trim();
                        i++;
                    }
//                    Populate view
                    urlDisplayView.findViewById(R.id.synonymTextView).post(() ->
                            ((TextView) urlDisplayView.findViewById(R.id.synonymTextView))
                                    .setText(data[0]));
                    urlDisplayView.findViewById(R.id.englishTextView).post(() ->
                            ((TextView) urlDisplayView.findViewById(R.id.englishTextView))
                                    .setText(data[2]));
                    urlDisplayView.findViewById(R.id.episodeTextView).post(() ->
                            ((TextView) urlDisplayView.findViewById(R.id.episodeTextView))
                                    .setText(data[4]));
                    urlDisplayView.findViewById(R.id.statusTextView).post(() ->
                            ((TextView) urlDisplayView.findViewById(R.id.statusTextView))
                                    .setText(data[5]));
                    urlDisplayView.findViewById(R.id.airingTextView).post(() ->
                            ((TextView) urlDisplayView.findViewById(R.id.airingTextView))
                                    .setText(data[6]));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.w("populateAnimeInfo", exception.toString());
                    throw new java.lang.UnsupportedOperationException();
                }
            }
        });
    }

    public void populateCoverImage(View urlDisplayView) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(mURL).get();
                    Element animeImageName = document.selectFirst("h1.title-name.h1_bold_none");
                    Element imageURL = document.selectFirst("img[alt*=" + animeImageName.text() + "]");
                    InputStream inputStream = (InputStream) new URL(imageURL.absUrl("data-src")).getContent();
//                    InputStream tester = (InputStream) new URL("https://d2y6mqrpjbqoe6.cloudfront.net/image/upload/f_auto,q_auto/cdn1/press/zoom-yuki-kiajura/kv_sao.jpg").getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    final Drawable coverDrawable = Drawable.createFromStream(tester, animeImageName.text());
//                    final Bitmap scaledBitmap = ImageEditor.resize(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2);
                    urlDisplayView.findViewById(R.id.coverImageView).post(() ->
                            ((ImageView) urlDisplayView.findViewById(R.id.coverImageView))
                                    .setImageBitmap(bitmap));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.w("populateCoverImage", exception.toString());
                    throw new java.lang.UnsupportedOperationException();
                }
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}