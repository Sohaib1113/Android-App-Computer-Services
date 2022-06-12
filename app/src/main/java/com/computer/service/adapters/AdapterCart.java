package com.computer.service.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.computer.service.R;
import com.computer.service.activities.ActivityCart;

// adapter class for custom order list
public class AdapterCart extends BaseAdapter {
		private LayoutInflater inflater;
		
		public AdapterCart(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return ActivityCart.Menu_ID.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			
			if(convertView == null){
				convertView = inflater.inflate(R.layout.lsv_item_order_list, null);
				holder = new ViewHolder();
				holder.txtMenuName = (TextView) convertView.findViewById(R.id.txtMenuName);
				holder.txtQuantity = (TextView) convertView.findViewById(R.id.txtQuantity);
				holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			holder.txtMenuName.setText(ActivityCart.Menu_name.get(position));
			holder.txtQuantity.setText(String.valueOf(ActivityCart.Quantity.get(position)));
			holder.txtPrice.setText(ActivityCart.Sub_total_price.get(position)+" "+ActivityCart.Currency);
			
			return convertView;
		}
		
		static class ViewHolder {
			TextView txtMenuName, txtQuantity, txtPrice;
		}
		
		
	}