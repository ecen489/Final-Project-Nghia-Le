package com.company.eventcountdown;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private int[] iconList = {
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
            R.drawable.a034, R.drawable.a036,
            R.drawable.a037, R.drawable.a038,
            R.drawable.a039, R.drawable.a040};

    public MyRecyclerViewAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, final int position) {
        final Event event = eventList.get(position);

        holder.textViewDate.setText(event.getEventDate());
        holder.textViewName.setText(event.getEventName());
        holder.textViewTime.setText(event.getEventTime());
        holder.image.setImageResource(iconList[event.getEventIcon()]);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public String getUUID(int position) {
        return eventList.get(position).getIdentifier();
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewDate, textViewName, textViewTime;
        ImageView image;

        public EventViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewDate = itemView.findViewById(R.id.eventDateText);
            textViewName = itemView.findViewById(R.id.eventNameText);
            textViewTime = itemView.findViewById(R.id.eventTimeText);
            image = itemView.findViewById(R.id.imageView);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
