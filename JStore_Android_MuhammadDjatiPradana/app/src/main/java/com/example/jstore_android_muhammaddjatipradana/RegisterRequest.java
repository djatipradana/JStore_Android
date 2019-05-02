package com.example.jstore_android_muhammaddjatipradana;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String Regis_URL = "http://192.168.137.184:8080/newcustomer";
    private Map<String, String> params;

    public RegisterRequest(String name, String email, String password, Response.Listener<String> listener){
        super(Method.POST, Regis_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("username", "defaultUsername");
        params.put("password", password);
        params.put("year", "2019");
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}


