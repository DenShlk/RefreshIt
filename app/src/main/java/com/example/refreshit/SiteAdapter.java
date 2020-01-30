package com.example.refreshit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Класс заполняет RecyclerView элементами (сайтами которые нужно чекать)
public class SiteAdapter extends RecyclerView.Adapter<ViewHolder> {
	private LayoutInflater inflater;
	private List<RefreshPage> pages;

	SiteAdapter(Context context, List<RefreshPage> pages) {
		this.pages = pages;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = inflater.inflate(R.layout.active_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		RefreshPage page = pages.get(position);
		holder.nameText.setText(page.name);
		holder.delayText.setText(page.delay_s);
	}

	@Override
	public int getItemCount() {
		return pages.size();
	}

}
