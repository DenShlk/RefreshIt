package com.example.refreshit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ViewHolder> {
	private LayoutInflater inflater;
	private List<RefreshPage> pages;

	ActiveAdapter(Context context, List<RefreshPage> pages) {
		this.pages = pages;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public ActiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = inflater.inflate(R.layout.active_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ActiveAdapter.ViewHolder holder, int position) {
		RefreshPage page = pages.get(position);
		holder.nameText.setText(page.name);
		holder.delayText.setText(page.delay_s);
	}

	@Override
	public int getItemCount() {
		return pages.size();
	}

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
	
}
