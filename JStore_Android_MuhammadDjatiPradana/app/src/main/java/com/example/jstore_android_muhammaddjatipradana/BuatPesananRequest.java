package com.example.jstore_android_muhammaddjatipradana;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuatPesananRequest extends StringRequest {
    private static final String PAID_URL = "http://192.168.1.6:8080/createinvoicepaid";
    private static final String UNPAID_URL = "http://192.168.1.6:8080/createinvoiceunpaid";
    private static final String INSTALLMENT_URL = "http://192.168.1.6:8080/createinvoiceinstallment";

    private Map<String, String> params;

    public BuatPesananRequest(ArrayList<Integer> listItem, int id_customer, Response.Listener<String> listener) {
        super(Method.POST, PAID_URL, listener, null);
        params = new HashMap<String, String>();
        params.put("listItem", listItem.get(0)+"");
        params.put("customerID", id_customer+"");
    }

    public BuatPesananRequest(ArrayList<Integer> listItem, int id_customer, String unpaid, Response.Listener<String> listener) {
        super(Method.POST, UNPAID_URL, listener, null);
        params = new HashMap<String, String>();
        params.put("listItem", listItem.get(0)+"");
        params.put("customerID", id_customer+"");
    }

    public BuatPesananRequest(ArrayList<Integer> listItem, int installmentPeriod, int id_customer, String installment, Response.Listener<String> listener) {
        super(Method.POST, INSTALLMENT_URL, listener, null);
        params = new HashMap<String, String>();
        params.put("listItem", listItem.get(0)+"");
        params.put("installmentPeriod",installmentPeriod+"");
        params.put("customerID", id_customer+"");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
