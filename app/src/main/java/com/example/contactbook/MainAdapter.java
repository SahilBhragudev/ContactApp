package com.example.contactbook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    Activity activity;
    ArrayList<ContactApp> arrayList;


    public MainAdapter(Activity activity, ArrayList<ContactApp> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);


        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        ContactApp model = arrayList.get(position);

        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getNumber());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv1);
            tvNumber = itemView.findViewById(R.id.tv2);
        }
    }
}
