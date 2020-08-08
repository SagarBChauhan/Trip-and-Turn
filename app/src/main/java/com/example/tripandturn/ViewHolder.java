package com.example.tripandturn;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
    }

    public void setDetails(Context context,String title, String description,String image){
        TextView mTitleTv=mView.findViewById(R.id.rTitleTv);
        TextView mDescriptionTv=mView.findViewById(R.id.rDescriptionTv);
        ImageView mImageIv=mView.findViewById(R.id.rImageView);

        mTitleTv.setText(title);
        mDescriptionTv.setText(description);
        Picasso.with(context).load(image).into(mImageIv);
    }
}
