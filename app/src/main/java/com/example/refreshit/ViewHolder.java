package com.example.refreshit;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Непосредственно создаем элемет списка
class ViewHolder extends RecyclerView.ViewHolder {

	final TextView nameText, delayText;
	private final Button archiveButton, changeButton, deleteButton;
	private final MainActivity activity;

	ViewHolder(View view, Context context, final boolean isActive) {
		super(view);
		this.activity = (MainActivity) context;
		this.nameText = view.findViewById(R.id.name);
		this.delayText = view.findViewById(R.id.delay);
		this.archiveButton = view.findViewById(R.id.arhive_button);
		archiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive)
					activity.activeItemClick(v, getAdapterPosition());
				else
					activity.archiveItemClick(v, getAdapterPosition());
			}
		});
		this.changeButton = view.findViewById(R.id.change_button);
		changeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive)
					activity.activeItemClick(v, getAdapterPosition());
				else
					activity.archiveItemClick(v, getAdapterPosition());
			}
		});
		this.deleteButton = view.findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new View.OnClickListener() {
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