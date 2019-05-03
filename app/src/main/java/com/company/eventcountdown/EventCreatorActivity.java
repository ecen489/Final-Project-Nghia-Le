package com.company.eventcountdown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EventCreatorActivity extends Activity {
    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private ImageButton eventIcon;
    private EditText eventNotification;
    private Button addEvent;
    private ImageButton speechButton;
    private FirebaseAuth mAuth;
    private DatePickerDialog picker;
    private TimePickerDialog picker2;
    private int imagePos = 4;
    private AlertDialog dialog;
    private final int REQUEST_SPEECH_RECOGNIZER = 3000;
    private String mAnswer = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);

        speechButton = findViewById(R.id.speechToTextBtn);
        eventName = findViewById(R.id.eventNameText);
        eventDate = findViewById(R.id.dateText);
        eventTime = findViewById(R.id.timeText);
        eventIcon = findViewById(R.id.iconText);
        eventNotification = findViewById(R.id.notificationText);
        addEvent = findViewById(R.id.addEventButton);

        mAuth = FirebaseAuth.getInstance();

        // Ensure user is logged in, go back to login activity if not
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        speechButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View arg0) {
                startSpeechRecognizer();
            }
        });

        eventDate.setInputType(InputType.TYPE_NULL);
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDate.setEnabled(false);
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // Date picker dialog
                picker = new DatePickerDialog(EventCreatorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);
                picker.show();
                eventDate.setEnabled(true);
            }
        });

        eventTime.setInputType(InputType.TYPE_NULL);
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTime.setEnabled(false);
                final Calendar cldr2 = Calendar.getInstance();
                int hour = cldr2.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr2.get(Calendar.MINUTE);
                // Time picker dialog
                picker2 = new TimePickerDialog(EventCreatorActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                            cldr2.set(Calendar.HOUR_OF_DAY, sHour);
                            cldr2.set(Calendar.MINUTE, sMinute);
                            SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
                            String time = mSDF.format(cldr2.getTime());
                            eventTime.setText(time);
                        }
                }, hour, minutes, false);
                picker2.show();
                eventTime.setEnabled(true);
            }
        });

        eventNotification.setInputType(InputType.TYPE_NULL);
        eventNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventNotification.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(EventCreatorActivity.this);
                builder.setTitle("Notification");

                String[] notificationTimes = {"At time of event", "30 minutes before", "1 hour before", "1 day before", "None"};
                builder.setItems(notificationTimes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                eventNotification.setText("At time of event");
                                eventNotification.setEnabled(true);
                                break;
                            case 1:
                                eventNotification.setText("30 minutes before");
                                eventNotification.setEnabled(true);
                                break;
                            case 2:
                                eventNotification.setText("1 hour before");
                                eventNotification.setEnabled(true);
                                break;
                            case 3:
                                eventNotification.setText("1 day before");
                                eventNotification.setEnabled(true);
                                break;
                            case 4:
                                eventNotification.setText("None");
                                eventNotification.setEnabled(true);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        eventIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIcon.setEnabled(false);
                final GridView gridView = new GridView(EventCreatorActivity.this);

                gridView.setAdapter(new AlertDialogImageAdapter(EventCreatorActivity.this));
                gridView.setNumColumns(4);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        eventIcon.setEnabled(true);
                        eventIcon.setImageResource(iconList[position]);
                        imagePos = position;
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(EventCreatorActivity.this);
                builder.setView(gridView);
                builder.setTitle("Icon");
                dialog = builder.show();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start create an account activity
                if(validate()) {
                    Intent intent = new Intent();
                    intent.putExtra("event_name", eventName.getText().toString());
                    intent.putExtra("event_date", eventDate.getText().toString());
                    intent.putExtra("event_time", eventTime.getText().toString());
                    intent.putExtra("event_notification", eventNotification.getText().toString());
                    intent.putExtra("event_icon", imagePos);
                    setResult(6969, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Please finish filling out the event form",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mQuestion);
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mAnswer = results.get(0);

                if (mAnswer.toUpperCase().indexOf("CREATE EVENT") > -1) {
                    if(validate()) {
                        Intent intent = new Intent();
                        intent.putExtra("event_name", eventName.getText().toString());
                        intent.putExtra("event_date", eventDate.getText().toString());
                        intent.putExtra("event_time", eventTime.getText().toString());
                        intent.putExtra("event_notification", eventNotification.getText().toString());
                        intent.putExtra("event_icon", imagePos);
                        setResult(6969, intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"Please finish filling out the event form",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    eventName.setText(mAnswer);
                }
            }
        }
    }


    private boolean validate() {
        boolean valid = true;
        if(eventName.getText().toString().equals("")
                || eventDate.getText().toString().equals("Date")
                || eventTime.getText().toString().equals("Time")
                || eventNotification.getText().toString().equals("Notification")) {
            valid = false;
        }
        return valid;
    }
}
