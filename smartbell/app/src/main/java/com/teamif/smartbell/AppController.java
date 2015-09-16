package com.teamif.smartbell;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by user on 2015-02-10.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }




    public static void SetJSONObjectAsVistorData(VisitorData visitorData, JSONObject obj) {

        try {

            visitorData.setVisitorSeqNo(obj.getString("seq_no"));

            visitorData.setName(obj.getString("visitor_name"));
            //name = visitorData.getName();
            visitorData.setThumbnailURL(obj
                    .getString("picture_path"));
            //thumbnail = visitorData.getThumbnailURL();
            visitorData.setPurpose((obj.getString("purpose")));
            //purpose = visitorData.getPurpose();

            visitorData.setTime(obj
                    .getString("visit_time"));

            visitorData.setFeedback(obj
                    .getString("feedback"));
            //time = visitorData.getTime();

            // Genre is json array
            // JSONArray genreArry =
            // obj.getJSONArray("genre");
            //ArrayList<String> genre = new ArrayList<String>();

            //genre.add(obj.getString("visitor_number"));
            //phone = obj.getString("visitor_phone");


            visitorData.setVisitorID(obj.getString("visitor_phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
