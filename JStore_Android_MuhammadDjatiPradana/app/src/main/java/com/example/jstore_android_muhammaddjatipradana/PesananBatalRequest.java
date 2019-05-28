package com.example.jstore_android_muhammaddjatipradana;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PesananBatalRequest extends StringRequest {
    private static final String BASE_URL = "http://192.168.1.6:8080/canceltransaction";
    private Map<String, String> params;

    public PesananBatalRequest(int id_invoice, Response.Listener<String> listener) {
        super(Method.POST, BASE_URL, listener, null);
        params = new HashMap<>();
        params.put("invoiceID", id_invoice+"");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
