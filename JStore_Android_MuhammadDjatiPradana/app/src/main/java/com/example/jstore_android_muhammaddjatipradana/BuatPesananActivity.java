package com.example.jstore_android_muhammaddjatipradana;

import android.content.Intent;
import android.graphics.RadialGradient;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuatPesananActivity extends AppCompatActivity {
    private int installmentPeriod = 0;
    private String selectedPayment;

    private ArrayList<Integer> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pesanan);

        final int id_item = getIntent().getIntExtra("id_item", 0);
        final String itemName = getIntent().getStringExtra("itemName");
        final double itemPrice = getIntent().getIntExtra("itemPrice",0);
        final String itemCategory = getIntent().getStringExtra("itemCategory");
        final String itemStatus = getIntent().getStringExtra("itemStatus");
        final int currentUserId = getIntent().getIntExtra("id_customer",0);
        listItem = new ArrayList<>();
        listItem.add(id_item);

        final Button order = (Button) findViewById(R.id.pesan);
        final TextView textPeriod = (TextView) findViewById(R.id.textPeriod);
        final EditText installment_period = (EditText) findViewById(R.id.installment_period);

        final TextView item_name = (TextView) findViewById(R.id.item_name);
        final TextView item_category = (TextView) findViewById(R.id.item_category);
        final TextView item_status = (TextView) findViewById(R.id.item_status);
        final TextView item_price = (TextView) findViewById(R.id.item_price);
        final TextView total_price = (TextView) findViewById(R.id.total_price);

        final Button hitung = (Button) findViewById(R.id.hitung);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        order.setVisibility(View.GONE);
        textPeriod.setVisibility(View.GONE);
        installment_period.setVisibility(View.GONE);

        item_name.setText(itemName);
        item_category.setText(itemCategory);
        item_status.setText(itemStatus);
        item_price.setText(String.format("%s", itemPrice));
        total_price.setText("0");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.paid:
                        selectedPayment = "PAID";
                        textPeriod.setVisibility(View.GONE);
                        installment_period.setText("");
                        installment_period.setVisibility(View.GONE);
                        hitung.setVisibility(View.VISIBLE);
                        order.setVisibility(View.GONE);
                        total_price.setText("0");
                        break;
                    case R.id.unpaid:
                        selectedPayment = "UNPAID";
                        textPeriod.setVisibility(View.GONE);
                        installment_period.setText("");
                        installment_period.setVisibility(View.GONE);
                        hitung.setVisibility(View.VISIBLE);
                        order.setVisibility(View.GONE);
                        total_price.setText("0");
                        break;
                    case R.id.installment:
                        selectedPayment = "INSTALLMENT";
                        textPeriod.setVisibility(View.VISIBLE);
                        installment_period.setVisibility(View.VISIBLE);
                        order.setVisibility(View.GONE);
                        hitung.setVisibility(View.VISIBLE);
                        order.setVisibility(View.GONE);
                        total_price.setText("0");
                        break;
                }
            }
        });

        installment_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitung.setVisibility(View.VISIBLE);
                order.setVisibility(View.GONE);
            }
        });

        hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPayment.equals("PAID") || selectedPayment.equals("UNPAID")){
                    total_price.setText(item_price.getText().toString());
                    order.setVisibility(View.VISIBLE);
                }
                else if(selectedPayment.equals("INSTALLMENT")){
                    String period = installment_period.getText().toString();
                    if(!period.equals("")){
                        installmentPeriod = Integer.parseInt(period);
                    }
                    if (installmentPeriod == 0) {
                        installment_period.setError("Period must be greater than 0");
                        installment_period.requestFocus();
                        return;
                    }
                    double total = itemPrice/installmentPeriod;
                    total_price.setText(String.format("%s", total));
                    order.setVisibility(View.VISIBLE);
                }
                hitung.setVisibility(View.GONE);
            }
        });

       order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if(jsonResponse != null){
                                AlertDialog.Builder builder = new AlertDialog.Builder(BuatPesananActivity.this);
                                builder.setMessage("Order Success").create().show();
                                Intent mainActivityIntent = new Intent(BuatPesananActivity.this, BuatPesananActivity.class);
                                //finish();
                            }
                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BuatPesananActivity.this);
                            builder.setMessage("Order Failed").create().show();
                        }
                    }
                };

                if(selectedPayment.equals("PAID")){
                    BuatPesananRequest buatPesananRequest = new BuatPesananRequest(listItem, currentUserId, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                    queue.add(buatPesananRequest);
                }
                else if(selectedPayment.equals("UNPAID")){
                    BuatPesananRequest buatPesananRequest = new BuatPesananRequest(listItem, currentUserId, "unpaid", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                    queue.add(buatPesananRequest);
                }
                else if(selectedPayment.equals("INSTALLMENT")){
                    BuatPesananRequest buatPesananRequest = new BuatPesananRequest(listItem,  installmentPeriod, currentUserId, "installment", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                    queue.add(buatPesananRequest);
                }
            }
        });
    }
}
