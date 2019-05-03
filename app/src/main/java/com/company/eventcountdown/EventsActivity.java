package com.company.eventcountdown;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EventsActivity extends Activity {
    private Button addEventButton;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String user;
    private List<Event> eventList;
    AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        addEventButton = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Ensure user is logged in, go back to login activity if not
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Get UID of user
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start create an account activity
                Intent intent = new Intent(EventsActivity.this, EventCreatorActivity.class);
                startActivityForResult(intent, 6969);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(this, eventList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // Do something here for single click
            }
            @Override
            public void onLongClick(View view, final int position) {
                final String UUID = adapter.getUUID(position);
                new AlertDialog.Builder(EventsActivity.this)
                        .setTitle("Delete event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteEvent(UUID);
                                eventList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        }));

        Query query = FirebaseDatabase.getInstance().getReference("events/")
                .orderByChild("uid")
                .equalTo(user);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            eventList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    eventList.add(event);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //switch(requestCode) {
        //    case 6969:
        //        if(resultCode == RESULT_OK) {
        //
        //        } else if (resultCode == RESULT_CANCELED) {
        //
        //        }
        //        break;
        //}
        if(requestCode==6969)
        {
            String eventName = data.getStringExtra("event_name");
            String eventDate = data.getStringExtra("event_date");

            // Split date into int variables
            String[] values = eventDate.split("/");
            int month = Integer.parseInt(values[0]);
            int day = Integer.parseInt(values[1]);
            int year = Integer.parseInt(values[2]);

            String eventTime = data.getStringExtra("event_time");
            //Split time into int variables
            String formatted = "00:00";
            try {
                Date date = new SimpleDateFormat("hh:mm a").parse(eventTime);
                formatted = new SimpleDateFormat("HH:mm").format(date);
            } catch ( ParseException e ) {
                e.printStackTrace();
            }
            String[] values2 = formatted.split(":");
            int hour = Integer.parseInt(values2[0]);
            int minute = Integer.parseInt(values2[1]);

            long future = countdownToFutureData(day, month, year, hour, minute);

            String eventNotification = data.getStringExtra("event_notification");

            switch (eventNotification) {
                case "At time of event":
                    break;
                case "30 minutes before":
                    future = future - 1800000;
                    break;
                case "1 hour before":
                    future = future - 3600000;
                    break;
                case "1 day before":
                    future = future - 86400000;
                    break;
                case "None":
                    break;
            }

            if(!eventNotification.equals("None")) {
                // Create a notification
                NotificationChannel notificationChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel("default",
                            "primary", NotificationManager.IMPORTANCE_DEFAULT);

                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) manager.createNotificationChannel(notificationChannel);

                    mAlarmManager=(AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
                    PendingIntent intent=PendingIntent.getBroadcast(getApplicationContext(),1234,
                            new Intent(getApplicationContext(),NotificationUpdate.class),0);

                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + future, intent);
                }
            }


            Integer eventIcon = data.getIntExtra("event_icon", 0);

            // Generates unique id for each event
            UUID uuid = UUID.randomUUID();
            String identifier = uuid.toString();

            // Push event to database
            Event event = new Event(eventDate, eventIcon, eventName, eventNotification, eventTime, user, identifier);
            DatabaseReference insLoc = databaseReference.child("events/");
            DatabaseReference ranKey = insLoc.push();
            ranKey.setValue(event);

            // Repopulate Recycler view after creating an event
            Query query = FirebaseDatabase.getInstance().getReference("events/")
                    .orderByChild("uid")
                    .equalTo(user);
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    public void deleteEvent(final String UUID) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                {
                    if(dataSnapshot1.child("identifier").getValue().toString().equals(UUID))
                    {
                        dataSnapshot1.getRef().setValue(null);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public long countdownToFutureData(int fday, int fmonth, int fyear, int fhour, int fmin) {

        Calendar futureDay = Calendar.getInstance(Locale.getDefault());
        futureDay.setTime(new Date(0)); /* reset */
        futureDay.set(Calendar.DAY_OF_MONTH, fday);
        futureDay.set(Calendar.MONTH, fmonth - 1); // 0-11 so 1 less
        futureDay.set(Calendar.YEAR, fyear);
        futureDay.set(Calendar.HOUR_OF_DAY, fhour);
        futureDay.set(Calendar.MINUTE, fmin);

        Calendar today = Calendar.getInstance(Locale.getDefault());
        long futureInMillis =  futureDay.getTimeInMillis() - today.getTimeInMillis();

        return futureInMillis;
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clicklistener) {
            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null &&gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
