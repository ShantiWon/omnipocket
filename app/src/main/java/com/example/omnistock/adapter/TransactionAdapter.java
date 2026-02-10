package com.example.omnistock.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omnistock.R;
import com.example.omnistock.model.Transaction;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        holder.transactionId.setText(String.valueOf(transaction.getTransactId()));
        holder.transactionDate.setText(transaction.getCreatedAt());
        holder.transactionItems.setText(transaction.getItemsSummary());
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        holder.transactionTotal.setText(formatter.format(transaction.getTotalAmount()));
        
        holder.transactionStatus.setText(transaction.getStatus());

        switch (transaction.getStatus().toLowerCase()) {
            case "completed":
                holder.transactionStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "pending":
                holder.transactionStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "received":
                holder.transactionStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
                break;
            default:
                holder.transactionStatus.setTextColor(Color.parseColor("#9E9E9E")); // Gray
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionId, transactionDate, transactionItems, transactionTotal, transactionStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionId = itemView.findViewById(R.id.transactionId);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            transactionItems = itemView.findViewById(R.id.transactionItems);
            transactionTotal = itemView.findViewById(R.id.transactionTotal);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
        }
    }
}
