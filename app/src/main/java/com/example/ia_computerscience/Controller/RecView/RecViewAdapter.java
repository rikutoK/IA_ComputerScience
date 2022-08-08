package com.example.ia_computerscience.Controller.RecView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ia_computerscience.Model.Public_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipeList;
    private OnViewClickListner onViewClickListner;

    private StorageReference storageReference;
    final private long FIVE_MEGABYTE = 1024 * 1024 * 5;

    private Map<String, Bitmap> images;

    public RecViewAdapter(Context context, ArrayList<Recipe> recipeList, OnViewClickListner onViewClickListner) {
        this.context = context;
        this.recipeList = recipeList;
        this.onViewClickListner = onViewClickListner;

        storageReference = FirebaseStorage.getInstance().getReference();

        images = new HashMap<>();
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

        if(recipeList.get(position) instanceof Public_Recipe) {
            holder.txtLikes.setVisibility(View.VISIBLE);
            holder.txtLikes.setText(((Public_Recipe) recipeList.get(position)).getLikes() + "");
        }
        else {
            holder.txtLikes.setVisibility(View.INVISIBLE);
        }


        if(images.containsKey(recipeList.get(position).getImageID())) { //if image already loaded
            holder.imageView.setImageBitmap(images.get(recipeList.get(position).getImageID()));
        }
        else { //get image from storage
            storageReference = FirebaseStorage.getInstance().getReference(Constants.IMAGE_PATH + recipeList.get(position).getImageID());
            storageReference.getBytes(FIVE_MEGABYTE)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                            images.put(recipeList.get(position).getImageID(), bitmap);
                            holder.imageView.setImageBitmap(bitmap);
                        }
                        else {
                            Toast.makeText(context, "Image failed to load", Toast.LENGTH_LONG).show();
                        }
                    });
        }



    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipeList(ArrayList<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
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
        TextView txtLikes;

        public MyViewHolder(@NonNull View itemView, OnViewClickListner onViewClickListner) {
            super(itemView);

            this.onViewClickListner = onViewClickListner;

            imageView = itemView.findViewById(R.id.RecView_imageView);
            txtName = itemView.findViewById(R.id.RecView_txtName);
            txtCal = itemView.findViewById(R.id.RecView_txtCal);
            txtTime = itemView.findViewById(R.id.RecView_txtTime);
            txtLikes = itemView.findViewById(R.id.RecView_txtLikes);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onViewClickListner.onViewClick(getAdapterPosition());
        }
    }
}
