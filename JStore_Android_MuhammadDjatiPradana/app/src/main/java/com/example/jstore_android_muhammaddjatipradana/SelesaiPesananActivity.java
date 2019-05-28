package com.example.jstore_android_muhammaddjatipradana;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelesaiPesananActivity extends AppCompatActivity {
    TextView tvDate, tvId, tvPayment, tvDueDate, tvItem, tvStatusCategory, tvPrice, tvTotalPrice, tvInstallmentPeriod;
    Button btnCancel, btnFinish;
    private int id_invoice;
    private String transaksi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_pesanan);

        tvDate = findViewById(R.id.tvDate);
        tvId = findViewById(R.id.tvId);
        tvPayment = findViewById(R.id.tvPayment);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvItem = findViewById(R.id.tvItem);
        tvStatusCategory = findViewById(R.id.tvStatusCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvInstallmentPeriod = findViewById(R.id.tvInstallmentPeriod);


        btnCancel = findViewById(R.id.btnCancel);
        btnFinish = findViewById(R.id.btnFinish);

        id_invoice = getIntent().getExtras().getInt("id_invoice");
        Log.d("invoice id", String.valueOf(id_invoice));

        textInit();

       // order.setVisibility(View.GONE);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Invoicee.setActive(false);
                                transaksi = "finish";
                                btnCancel.setVisibility(View.GONE);
                                btnFinish.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Invoice Cancelled!").create().show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Operation Failed! Please try again").create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Invoicee.setActive(false);
                            transaksi = "finish";
                            btnCancel.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Invoice Cancelled!").create().show();
                        }
                    }
                };

                PesananBatalRequest request = new PesananBatalRequest(id_invoice, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(request);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Invoicee.setActive(false);
                                transaksi = "finish";
                                btnCancel.setVisibility(View.GONE);
                                btnFinish.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Invoice Finished!").create().show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Operation Failed! Please try again").create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Operation Failed! Please try again").create().show();
                        }
                    }
                };
                PesananSelesaiRequest request = new PesananSelesaiRequest(id_invoice, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(request);
            }
        });
    }

    public void textInit() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("jsonobject", "onResponse: " + jsonObject);
                    if (jsonObject!=null){
                        String date = jsonObject.getString("date");
                        JSONArray item_json = jsonObject.getJSONArray("item");

                        String invoiceType = jsonObject.getString("invoiceType");
                        Log.d("invoiectype", String.valueOf(invoiceType));

                        String invoiceStatus = jsonObject.getString("invoiceStatus");
                        Integer totalPrice = jsonObject.getInt("totalPrice");

                        tvStatusCategory.setText(invoiceStatus);
                        tvDate.setText("Date: " + date);
                        tvId.setText("Invoice ID: " + id_invoice);
                        tvItem.setText(String.valueOf(item_json));
                        tvPayment.setText(invoiceType);

                        tvDueDate.setVisibility(View.GONE);
                        tvInstallmentPeriod.setVisibility(View.GONE);
                        tvPrice.setVisibility(View.GONE);
                        switch (invoiceStatus) {
                            case "Unpaid":
                                tvDueDate.setVisibility(View.VISIBLE);
                                String dueDate = jsonObject.getString("dueDate");
                                tvDueDate.setText("Due Date: " + dueDate);
                                tvTotalPrice.setText("Rp. " + totalPrice);
                                break;
                            case "Installment":
                                tvInstallmentPeriod.setVisibility(View.VISIBLE);
                                tvPrice.setVisibility(View.VISIBLE);
                                String installmentPeriod = jsonObject.getString("installmentPeriod");
                                String installmentPrice = jsonObject.getString("installmentPrice");
                                tvTotalPrice.setText("Rp. " + installmentPrice);
                                tvPrice.setText("Rp. " + totalPrice);
                                tvInstallmentPeriod.setText("Installment Period: "+ installmentPeriod);
                                break;
                        }

                        if ((jsonObject.getString("isActive").equals("false") && invoiceStatus.equals("Paid"))) {
                            tvTotalPrice.setText("Rp. " + totalPrice);
                            //btnCancel.setEnabled(false);
                            btnCancel.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Transaksi Sudah Selesai").create().show();
                        }

                        else if (jsonObject.getString("isActive").equals("false") && (invoiceStatus.equals("Unpaid"))) {
                            btnCancel.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Transaksi Sudah Selesai").create().show();
                        }

                        else if (jsonObject.getString("isActive").equals("false") && invoiceStatus.equals("Installment")) {
                            btnCancel.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Transaksi Sudah Selesai").create().show();
                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        InvoiceFetchRequest request = new InvoiceFetchRequest(id_invoice, responseListener);
        RequestQueue queue = new Volley().newRequestQueue(SelesaiPesananActivity.this);
        queue.add(request);

    }
}
