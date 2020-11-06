package com.example.bearminimum;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * AppController
 *
 * Raises an small error message if can't connect
 * to Google book api
 *
 * Nov. 6, 2020
 */

public class AppController {

    private static com.example.bearminimum.AppController mInstance;

    private RequestQueue mRequestQueue;

    private static Context mCtx;

    private AppController(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**\
     * creates an instance of the AppController if it doesn't already exist
     *
     * @param context
     * @return mInstance    the AppController instance
     */

    public static synchronized com.example.bearminimum.AppController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new com.example.bearminimum.AppController(context);
        }
        return mInstance;
    }


    /**
     * creates a request queue if it doesn't already exist
     *
     * @return mRequestQueue    the request queue instance
     */

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }


    /**
     * adds a request to the request queue
     *
     * @param request
     * @param <T>
     */

    public <T> void addToRequestQueue(@NonNull final Request<T> request) {
        getRequestQueue().add(request);
    }


    /**
     * adds a request to the requests queue along with a tag
     *
     * @param request
     * @param tag
     * @param <T>
     */

    public <T> void addToRequestQueueWithTag(@NonNull final Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
}
