package com.example.reed.sparky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reed.sparky.R;
import com.example.reed.sparky.weather.Day;

/**
 * Created by reed_ on 8/31/2016.
 * Adapter to create a custom layout for the list view
 */
public class DayAdapter extends BaseAdapter{

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        setContext(context);
        setDays(days);
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;   //not used
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            //No view currently, need to create
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder((ImageView) convertView.findViewById(R.id.iconImageView),
                    (TextView) convertView.findViewById(R.id.temperatureLabel),
                    (TextView) convertView.findViewById(R.id.dayNameLabel));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = mDays[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");
        if (position == 0) {
            holder.dayLabel.setText("Today");
        }
        holder.dayLabel.setText(day.getDayOfTheWeek());

        return convertView;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView temperatureLabel;
        public TextView dayLabel;

        public ViewHolder(ImageView icon, TextView temp, TextView day) {
            iconImageView = icon;
            temperatureLabel = temp;
            dayLabel = day;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Day[] getDays() {
        return mDays;
    }

    public void setDays(Day[] days) {
        mDays = days;
    }
}
