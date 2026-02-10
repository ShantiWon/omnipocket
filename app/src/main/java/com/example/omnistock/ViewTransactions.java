package com.example.omnistock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omnistock.adapter.TransactionAdapter;
import com.example.omnistock.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewTransactions extends AppCompatActivity {

    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private TextView emptyText, titleText;
    private ImageButton backBtn;
    private String statusFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        titleText = findViewById(R.id.titleText);
        backBtn = findViewById(R.id.backBtn);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(adapter);

        // Get status filter from intent
        statusFilter = getIntent().getStringExtra("status");
        if (statusFilter != null) {
            titleText.setText(getTitleForStatus(statusFilter));
        }

        backBtn.setOnClickListener(v -> finish());

        loadTransactions();
    }

    private String getTitleForStatus(String status) {
        switch (status.toLowerCase()) {
            case "completed":
                return "Completed Orders";
            case "pending":
                return "Orders To Ship";
            case "received":
                return "Orders To Receive";
            default:
                return "My Purchases";
        }
    }

    private void loadTransactions() {
        SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        String url = "http://10.0.2.2/omnistock/getTransactions.php?user_id=" + userId;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            url += "&status=" + statusFilter;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            transactionList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject transactionObj = data.getJSONObject(i);
                                Transaction transaction = new Transaction(
                                        transactionObj.getInt("transact_id"),
                                        transactionObj.getInt("user_id"),
                                        transactionObj.getDouble("total_amount"),
                                        transactionObj.getString("status"),
                                        transactionObj.getString("created_at"),
                                        transactionObj.getString("items_summary")
                                );
                                transactionList.add(transaction);
                            }

                            adapter.notifyDataSetChanged();

                            if (transactionList.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                                transactionsRecyclerView.setVisibility(View.GONE);
                            } else {
                                emptyText.setVisibility(View.GONE);
                                transactionsRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
