package com.gso.hogoapi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gso.hogoapi.R;
import com.gso.hogoapi.model.History;

public class HistoryAdapter extends TypedListAdapter<History> {

	final LayoutInflater layoutInflater;
	
	public HistoryAdapter(Context context) {
		layoutInflater = LayoutInflater.from(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_history, parent, false);
			viewHolder = new ViewHolder(convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(position%2==0) {
			convertView.setBackgroundColor(Color.LTGRAY);
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}
		viewHolder.bind(getItem(position));
		return convertView;
	}
	
	static class ViewHolder {
		TextView tvDate;
		TextView tvTitle;
		TextView tvRecipient;
		TextView tvStatus;
		
		public ViewHolder(View view) {
			tvDate = (TextView) view.findViewById(R.id.tvDate);
			tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			tvRecipient = (TextView) view.findViewById(R.id.tvRecipient);
			tvStatus = (TextView) view.findViewById(R.id.tvStatus);
			view.setTag(this);
		}

		public void bind(History item) {
			tvDate.setText(item.createDate);
			tvTitle.setText(item.documentName);
			tvRecipient.setText(item.recipientName);
			tvStatus.setText("opened");
		}
	}

}
