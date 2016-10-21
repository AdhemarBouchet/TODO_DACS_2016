package edu.frcu.dacs.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.frcu.dacs.todo.R;
import edu.frcu.dacs.todo.models.ToDoEntry;

/**
 * Created by lkloster on 8/26/2016.
 */
public class RequestSingleton {
    private static RequestSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private static String host = "http://localhost:8200/api/";
    private static String obtener_todas = "tareas/todas";

    private RequestSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String token) {
        //Aqui agregar el token a la llamada menos las de login y register

        try {
            if (!req.getHeaders().get("avoidAuth").isEmpty()) {
                req.getHeaders().put("TOKEN", token);
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        getRequestQueue().add(req);
    }
}