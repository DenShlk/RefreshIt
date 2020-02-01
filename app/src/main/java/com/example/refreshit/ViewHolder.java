package com.example.refreshit;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Непосредственно создаем элемет списка
public class ViewHolder extends RecyclerView.ViewHolder {

	final TextView nameText, delayText;
	final Button archive_button, change_button, delete_button;
	final MainActivity activity;

	ViewHolder(View view, Context context, final boolean isActive) {
		super(view);
		this.activity = (MainActivity) context;
		this.nameText = view.findViewById(R.id.name);
		this.delayText = view.findViewById(R.id.delay);
		this.archive_button = view.findViewById(R.id.arhive_button);
		archive_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive)
					activity.activeItemClick(v, getAdapterPosition());
				else
					activity.archiveItemClick(v, getAdapterPosition());
			}
		});
		this.change_button = view.findViewById(R.id.change_button);
		change_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive)
					activity.activeItemClick(v, getAdapterPosition());
				else
					activity.archiveItemClick(v, getAdapterPosition());
			}
		});
		this.delete_button = view.findViewById(R.id.delete_button);
		delete_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive)
					activity.activeItemClick(v, getAdapterPosition());
				else
					activity.archiveItemClick(v, getAdapterPosition());
			}
		});
	}
}