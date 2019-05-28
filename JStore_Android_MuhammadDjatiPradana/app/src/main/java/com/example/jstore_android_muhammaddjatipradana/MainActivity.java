package com.example.jstore_android_muhammaddjatipradana;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private int currentUserId;
    private String currentName;
    private ArrayList<Supplier> listSupplier = new ArrayList<>();
    private ArrayList<Item> listItem = new ArrayList<>();
    private HashMap<Supplier, ArrayList<Item>> childMapping = new HashMap<>();
    private ArrayList<Invoicee> invoices = new ArrayList<>();
    RecyclerView rvOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final Button activeOrder = (Button) findViewById(R.id.activeOrder);
        //final Button orderHistory = (Button) findViewById(R.id.orderHistory);
        final Button logout = (Button) findViewById(R.id.logout);
        final TextView tvTest = (TextView) findViewById(R.id.tvTest);
        expListView = (ExpandableListView) findViewById(R.id.main);
        rvOrder = findViewById(R.id.rvOrder);


        currentUserId = this.getIntent().getIntExtra("id_customer",0);
        currentName = this.getIntent().getStringExtra("name_customer");

        tvTest.setText("Welcome, "+currentName+"!");

        refreshList();
        initRecyclerView();

     /*   logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences myPrefs = getSharedPreferences("MY",
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();
                //AppState.getSingleInstance().setLoggingOut(true);
                //Log.d(TAG, "Now log out and start the activity login");
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });     */

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SharedPreferences myPrefs = getSharedPreferences("Activity",
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();
                //AppState.getSingleInstance().setLoggingOut(true);
                setLoginState(true);
                //Log.d(TAG, "Now log out and start the activity login");
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        /*activeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActiveOrdersActivity.class);
                intent.putExtra("id_customer", currentUserId);
                startActivity(intent);
            }
        });

      orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                intent.putExtra("id_customer", currentUserId);
                startActivity(intent);
            }
        }); */

    }

    private void setLoginState(boolean status) {
        SharedPreferences sp = getSharedPreferences("LoginState",
                MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("setLoggingOut", status);
        ed.commit();
    }

    private void initRecyclerView(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonResponse = new JSONArray(response);
                    for(int i=0;i<jsonResponse.length();i++){
                        JSONObject invoice = jsonResponse.getJSONObject(i);

                        int id = invoice.getInt("id");
                        String date = invoice.getString("date");
                        JSONArray item_json = invoice.getJSONArray("item");

                        ArrayList<String> items = new ArrayList<>();
                        for(int y=0;y<item_json.length();y++){
                            for(Item item : listItem){
                                if(item.getId() == Integer.valueOf(item_json.get(y).toString().trim())){
                                    items.add(item.getName());
                                }
                            }
                        }

                        String invoiceType = invoice.getString("invoiceType");
                        String invoiceStatus = invoice.getString("invoiceStatus");
                        Integer totalPrice = invoice.getInt("totalPrice");

                        if(invoiceStatus.equals("Paid")){
                            Invoicee temp = new Invoicee(id, date, items, totalPrice, invoiceType, invoiceStatus);
                            temp.setActive(false);
                            invoices.add(temp);
                        }else if(invoiceStatus.equals("Unpaid")) {
                            Invoicee temp = new Invoicee(id, date, items, totalPrice, invoiceType, invoiceStatus);
                            temp.setActive(true);
                            invoices.add(temp);
                        }else if(invoiceStatus.equals("Installment")) {
                            int installmentPeriod = invoice.getInt("installmentPeriod");
                            int installmentPrice = invoice.getInt("installmentPrice");
                            Invoicee temp = new Invoicee(id, date, items, totalPrice, invoiceType, invoiceStatus, installmentPeriod, installmentPrice);
                            temp.setActive(true);
                            invoices.add(temp);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(invoices, MainActivity.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                rvOrder.setLayoutManager(layoutManager);
                rvOrder.setAdapter(adapter);
            }
        };

        PesananFetchRequest request = new PesananFetchRequest(currentUserId,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    protected void refreshList() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i=0; i<jsonResponse.length(); i++) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        JSONObject item = jsonResponse.getJSONObject(i);
                        JSONObject supplier = item.getJSONObject("supplier");
                        JSONObject location = supplier.getJSONObject("location");

                        Location newLocation = new Location(
                                location.getString("province"),
                                location.getString("description"),
                                location.getString("city")
                        );

                        Supplier newSupplier = new Supplier(
                                supplier.getInt("id"),
                                supplier.getString("name"),
                                supplier.getString("email"),
                                supplier.getString("phoneNumber"),
                                newLocation
                        );

                        Item newItem = new Item(
                                item.getInt("id"),
                                item.getString("name"),
                                item.getInt("price"),
                                item.getString("category"),
                                item.getString("status"),
                                newSupplier
                        );

                        listItem.add(newItem);

                        //Check if the Supplier already Exists
                        boolean tempStatus = true;
                        for(Supplier supplierPtr : listSupplier) {
                            if(supplierPtr.getId() == newSupplier.getId()){
                                tempStatus = false;
                            }
                        }
                        if(tempStatus==true){
                            listSupplier.add(newSupplier);
                        }
                    }

                    for(Supplier supplierPtr : listSupplier){
                        ArrayList<Item> tempItemList = new ArrayList<>();
                        for(Item itemPtr : listItem){
                            if(itemPtr.getSupplier().getId() == supplierPtr.getId()){
                                tempItemList.add(itemPtr);
                            }
                        }
                        childMapping.put(supplierPtr, tempItemList);
                    }

                    listAdapter = new MainListAdapter(MainActivity.this, listSupplier, childMapping);
                    expListView.setAdapter(listAdapter);

                    expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            Item selected = childMapping.get(listSupplier.get(groupPosition)).get(childPosition);
                            Log.d("msg", "item " + childPosition + " of group " + groupPosition + " clicked.");
                            Intent intent = new Intent(MainActivity.this, BuatPesananActivity.class);
                            intent.putExtra("id_item", selected.getId());
                            intent.putExtra("itemName", selected.getName());
                            intent.putExtra("itemPrice", selected.getPrice());
                            intent.putExtra("itemCategory", selected.getCategory());
                            intent.putExtra("itemStatus", selected.getStatus());
                            intent.putExtra("id_customer", currentUserId);
                            startActivity(intent);
                            return false;
                        }
                    });


                }
                catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Load Data Failed.").create().show();
                }
            }
        };

        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);
    }
}