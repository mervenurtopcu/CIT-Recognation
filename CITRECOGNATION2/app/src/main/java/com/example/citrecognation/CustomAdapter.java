package com.example.citrecognation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{


    private Context context;
    private Activity activity;
    private ArrayList record_id, record_text;

    CustomAdapter(Activity activity, Context context, ArrayList rec_id, ArrayList rec_text){
        this.activity = activity;
        this.context = context;
        this.record_id = rec_id;
        this.record_text = rec_text;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.record_id_txt.setText(String.valueOf(record_id.get(position)));
        holder.record_txt.setText(String.valueOf(record_text.get(position)));

        //Recyclerview onClickListener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdatePageActivity.class);
                intent.putExtra("id", String.valueOf(record_id.get(position)));
                intent.putExtra("text", String.valueOf(record_text.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    public int getItemCount() {
        return record_id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView cardView;
        TextView record_id_txt, record_txt;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            record_id_txt = itemView.findViewById(R.id.preview_id);
            record_txt = itemView.findViewById(R.id.preview_text);
            this.cardView = itemView.findViewById(R.id.card_view);

        }

    }
}
