package com.example.ia_computerscience.Controller.RecView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipeList;
    private OnViewClickListner onViewClickListner;

    private StorageReference storageReference;
    final private long FIVE_MEGABYTE = 1024 * 1024 * 5;

    public RecViewAdapter(Context context, ArrayList<Recipe> recipeList, OnViewClickListner onViewClickListner) {
        this.context = context;
        this.recipeList = recipeList;
        this.onViewClickListner = onViewClickListner;

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rec_view_row, parent, false);
        return new RecViewAdapter.MyViewHolder(view, onViewClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(recipeList.get(position).getName());
        holder.txtCal.setText(recipeList.get(position).getCalories() + "kcl");
        holder.txtTime.setText(recipeList.get(position).getTime());

        storageReference = FirebaseStorage.getInstance().getReference(Constants.IMAGE_PATH + recipeList.get(position).getImageID());
        storageReference.getBytes(FIVE_MEGABYTE)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                       holder.imageView.setImageBitmap(bitmap);
                   }
                });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface OnViewClickListner {
        void onViewClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnViewClickListner onViewClickListner;

        ImageView imageView;
        TextView txtName;
        TextView txtCal;
        TextView txtTime;

        public MyViewHolder(@NonNull View itemView, OnViewClickListner onViewClickListner) {
            super(itemView);

            this.onViewClickListner = onViewClickListner;

            imageView = itemView.findViewById(R.id.RecView_imageView);
            txtName = itemView.findViewById(R.id.RecView_txtName);
            txtCal = itemView.findViewById(R.id.RecView_txtCal);
            txtTime = itemView.findViewById(R.id.RecView_txtTime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onViewClickListner.onViewClick(getAdapterPosition());
        }
    }
}
