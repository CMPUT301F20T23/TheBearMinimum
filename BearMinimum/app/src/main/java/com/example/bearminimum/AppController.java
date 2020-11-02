package com.example.bearminimum;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController {

    private static com.example.bearminimum.AppController mInstance;

    private RequestQueue mRequestQueue;

    private static Context mCtx;

    private AppController(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized com.example.bearminimum.AppController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new com.example.bearminimum.AppController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(@NonNull final Request<T> request) {
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueueWithTag(@NonNull final Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
}
