package com.example.refreshit;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Непосредственно создаем элемет списка
public class ViewHolder extends RecyclerView.ViewHolder {

	final TextView nameText, delayText;
	final Button arhive_button, change_button, delete_button;

	ViewHolder(View view){
		super(view);
		this.nameText = view.findViewById(R.id.name);
		this.delayText = view.findViewById(R.id.delay);
		this.arhive_button = view.findViewById(R.id.arhive_button);
		this.change_button = view.findViewById(R.id.change_button);
		this.delete_button = view.findViewById(R.id.delete_button);
		//TODO: listeners fot item-buttons
	}
}
