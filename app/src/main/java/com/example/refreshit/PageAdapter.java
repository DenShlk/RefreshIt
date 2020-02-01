package com.example.refreshit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Класс заполняет RecyclerView элементами (сайтами которые нужно чекать)
public class PageAdapter extends RecyclerView.Adapter<ViewHolder> {
	private LayoutInflater inflater;
	private List<PageInfo> pages;
	Context context;
	boolean isActive;

	PageAdapter(Context context, List<PageInfo> pages, boolean isActive) {
		this.context = context;
		this.pages = pages;
		this.inflater = LayoutInflater.from(context);
		this.isActive = isActive;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = inflater.inflate(R.layout.active_list_item, parent, false);
		return new ViewHolder(view, context, isActive);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PageInfo page = pages.get(position);
		holder.nameText.setText(page.name);
		holder.delayText.setText(String.valueOf(page.delayTime) + " " + TimeUnit.values()[page.delayUnit].name());
	}

	@Override
	public int getItemCount() {
		return pages.size();
	}

}
