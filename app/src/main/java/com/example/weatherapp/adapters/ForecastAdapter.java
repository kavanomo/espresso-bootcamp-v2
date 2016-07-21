package com.example.weatherapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.WeatherAppSharedPrefs;
import com.example.weatherapp.activities.DetailsActivity;
import com.example.weatherapp.activities.MainActivity;
import com.example.weatherapp.data.WeatherContract;
import com.example.weatherapp.utils.WeatherUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForecastAdapter extends CursorRecyclerAdapter<ForecastAdapter.ViewHolder> {

    private static final int TODAY_CARD = 0;
    private static final int REGULAR_CARD = 1;

    @Inject
    WeatherAppSharedPrefs sharedPrefs;
    Context context;
    ItemClickCallback itemClickCallback;

    int selectedItem;

    public ForecastAdapter(Cursor cursor, ItemClickCallback itemClickCallback, int lastPosition) {
        super(cursor);
        this.selectedItem = lastPosition;
        this.itemClickCallback = itemClickCallback;
        WeatherAppApplication.getInstance().inject(this);
        context = WeatherAppApplication.getInstance().getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case TODAY_CARD:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_forecast_today, viewGroup, false);
                break;
            case REGULAR_CARD:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_forecast, viewGroup, false);
                break;
            default:
                return null;
        }
        return new ViewHolder(view, itemClickCallback);
    }

    @Override
    public int getItemViewType(int position) {
        long currentDay = new DateTime().withZone(DateTimeZone.getDefault()).withTimeAtStartOfDay().getMillis();
        long dateFromTheCursor;

        if (mDataValid && getCursor().moveToPosition(position)) {
            dateFromTheCursor = getCursor().getLong(getCursor().getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        } else {
            return 0;
        }

        if (dateFromTheCursor == currentDay && !MainActivity.isTabletLayout) {
            return TODAY_CARD;
        } else {
            return REGULAR_CARD;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Drawable cardImage;
        Boolean isMetric = sharedPrefs.isMetric();

        int maxTempColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int minTempColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int descriptionColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
        int weatherIdColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        int weatherId = cursor.getInt(weatherIdColumnIndex);

        Long date = cursor.getLong(dateColumnIndex);
        String dayName = WeatherUtils.getDayName(context, date);

        if (getItemViewType(cursor.getPosition()) == TODAY_CARD) {
            cardImage = ContextCompat.getDrawable(context, WeatherUtils.getArtResourceForWeatherCondition(weatherId));
            viewHolder.textView_day.setText(dayName+ new DateTime(date).toString(", MMM d"));
        } else {
            cardImage = ContextCompat.getDrawable(context, WeatherUtils.getIconResourceForWeatherCondition(weatherId));
            viewHolder.textView_day.setText(dayName);
        }

        viewHolder.imageview_icon.setImageDrawable(cardImage);
        viewHolder.textview_weather.setText(cursor.getString(descriptionColumnIndex));
        viewHolder.textview_maxTemp.setText(WeatherUtils.formatTemperature(context, cursor.getDouble(maxTempColumnIndex), isMetric));
        viewHolder.textview_minTemp.setText(WeatherUtils.formatTemperature(context, cursor.getDouble(minTempColumnIndex), isMetric));

        if (cursor.getPosition() == selectedItem) {
            viewHolder.itemView.setSelected(true);
            if (MainActivity.isTabletLayout) {
                ((CardView) viewHolder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
            } else {
                ((CardView) viewHolder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }
        } else {
            viewHolder.itemView.setSelected(false);
            ((CardView) viewHolder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }

    @Override
    public long getItemId(int position) {
        if (hasStableIds() && mDataValid) {
            if (mCursor.moveToPosition(position)) {
                return position;
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_view_day)
        TextView textView_day;
        @Bind(R.id.card_view_weather)
        TextView textview_weather;
        @Bind(R.id.card_view_max_temp)
        TextView textview_maxTemp;
        @Bind(R.id.card_view_min_temp)
        TextView textview_minTemp;
        @Bind(R.id.card_view_icon)
        ImageView imageview_icon;

        ItemClickCallback itemClickCallback;

        public ViewHolder(View itemView, ItemClickCallback itemClickCallback) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemClickCallback = itemClickCallback;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            selectedItem = position;
            notifyDataSetChanged();

            if (getCursor().moveToPosition(position)) {
                int columnIndex = getCursor().getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
                Uri weatherUri = WeatherContract.buildWeatherLocationWithDate(sharedPrefs.getLocationPrefs(), getCursor().getLong(columnIndex));
                Intent detailsActivityIntent = new Intent(view.getContext(), DetailsActivity.class);
                detailsActivityIntent.setData(weatherUri);
                itemClickCallback.onItemSelected(weatherUri);
            }
        }
    }

    public interface ItemClickCallback {
        void onItemSelected(Uri uri);
    }
}
