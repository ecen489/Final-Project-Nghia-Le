package com.company.eventcountdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class AlertDialogImageAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;

    AlertDialogImageAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return iconList.length;
    }

    @Override
    public Object getItem(int position) {
        return iconList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        AlertDialogViewHolder alertDialogViewHolder;

        if (convertView == null) {
            // This is an alertDialog, therefore it has no root
            convertView = layoutInflater.inflate(R.layout.view_icon_chooser_entry, null);

            DisplayMetrics metrics = convertView.getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;

            convertView.setLayoutParams(new GridView.LayoutParams(screenWidth / 7, screenWidth / 7));
            alertDialogViewHolder = new AlertDialogViewHolder();
            alertDialogViewHolder.icon = convertView.findViewById(R.id.image_choose_icon_entry);
            convertView.setTag(alertDialogViewHolder);
        } else {
            alertDialogViewHolder = (AlertDialogViewHolder) convertView.getTag();
        }

        alertDialogViewHolder.icon.setAdjustViewBounds(true);
        alertDialogViewHolder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        alertDialogViewHolder.icon.setPadding(8, 8, 8, 8);
        alertDialogViewHolder.icon.setImageResource(iconList[position]);
        return convertView;
    }

    // This is your source for your icons, fill it with your own
    private Integer[] iconList = {
            R.drawable.a001, R.drawable.a002,
            R.drawable.a003, R.drawable.a004,
            R.drawable.a005, R.drawable.a006,
            R.drawable.a007, R.drawable.a008,
            R.drawable.a009, R.drawable.a010,
            R.drawable.a011, R.drawable.a012,
            R.drawable.a013, R.drawable.a014,
            R.drawable.a015, R.drawable.a016,
            R.drawable.a017, R.drawable.a018,
            R.drawable.a019, R.drawable.a020,
            R.drawable.a021, R.drawable.a022,
            R.drawable.a023, R.drawable.a024,
            R.drawable.a025, R.drawable.a026,
            R.drawable.a027, R.drawable.a028,
            R.drawable.a029, R.drawable.a030,
            R.drawable.a031, R.drawable.a032,
            R.drawable.a033, R.drawable.a034,
            R.drawable.a035, R.drawable.a036,
            R.drawable.a037, R.drawable.a038,
            R.drawable.a039, R.drawable.a040
    };

    private class AlertDialogViewHolder {
        ImageView icon;
    }
}
