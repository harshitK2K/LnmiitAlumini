package com.example.android.finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView mProfileImageView;
    private TextView mNameTextView;
    private FirebaseAuth firebaseAuth;
    private TextView mStartDateVIew;
    private TextView mStartTimeView;
    private TextView mEndDateVIew;
    private TextView mEndTimeView;
    private Button mSaveButton;
    private Button mCancelButton;
    private Uri uriProfileImage;
    private EditText mTitleView;
    private EditText mDesciptionView;
    private EditText mLocationView;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Uri profileImageUrl;
    private String amPm;
    private int currentHour;
    private int currentMinute;
    private Button mAddPhotoButton;
    private Uri uri;
    private Calendar calendar;
    private static final int CHOOSE_IMAGE = 101;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        mProfileImageView = findViewById(R.id.iconImageView);
        firebaseAuth = FirebaseAuth.getInstance();
        mTitleView = findViewById(R.id.titleTextView);
        mDesciptionView = findViewById(R.id.descriptionTextView);
        mLocationView = findViewById(R.id.evenLocationVIew);
        mNameTextView = findViewById(R.id.nameTextView);
        mStartDateVIew = findViewById(R.id.startDate);
        mStartTimeView = findViewById(R.id.startTime);
        mEndDateVIew = findViewById(R.id.endDate);
        mEndTimeView = findViewById(R.id.endTime);
        mAddPhotoButton = findViewById(R.id.addPhoto);
        mSaveButton = findViewById(R.id.saveDetails);
        mCancelButton = findViewById(R.id.cancelDetails);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        mStartDateVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                DatePickerFragement dialogFragment = new DatePickerFragement();
                dialogFragment.show(getSupportFragmentManager(),"date picker");
            }
        });
        mEndDateVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                DatePickerFragement dialogFragment = new DatePickerFragement();
                dialogFragment.show(getSupportFragmentManager(),"date picker");
            }
        });
        mStartTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        mStartTimeView.setText(hourOfDay + ":"+ minutes + " " + amPm);
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });
        mEndTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        mEndTimeView.setText(hourOfDay + ":"+ minutes + " " + amPm);
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEvent.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadUserInformation();
            }
        });
    }

   private void UploadUserInformation(){
        String title = mTitleView.getText().toString().trim();
        String description = mDesciptionView.getText().toString().trim();
        String location =  mLocationView.getText().toString().trim();
        String startDate = mStartDateVIew.getText().toString().trim();
        String startTime = mStartTimeView.getText().toString();
        String endDate = mEndDateVIew.getText().toString();
        String endTime = mEndTimeView.getText().toString();
        uri = null;
        if(title.isEmpty()){
            mTitleView.setError("Title Is required");
            mTitleView.requestFocus();
            return;
        }
        if(description.isEmpty()){
            mDesciptionView.setError("Description Is required");
            mDesciptionView.requestFocus();
            return;
        }
        if(location.isEmpty()){
            mLocationView.setError("Location Is required");
            mLocationView.requestFocus();
            return;
        }
        if(startDate.isEmpty()){
            mStartDateVIew.setError("Field Is required");
            mStartDateVIew.requestFocus();
            return;
        }
        if(uriProfileImage != null) {
            storeImageToFirebase();
        }


        EventList eventList = new EventList(title,description,location,startDate,startTime,endDate,endDate,"");
        myRef.push().setValue(eventList);
        mTitleView.setText("");
        mDesciptionView.setText("");
        mLocationView.setText("");
        mStartDateVIew.setText("");
        mStartTimeView.setText("");
        mEndDateVIew.setText("");
        mEndTimeView.setText("");
        Toast.makeText(AddEvent.this,"Task Added Succesfully",Toast.LENGTH_SHORT).show();


    }

    private void storeImageToFirebase(){

        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        profileImageRef.putFile(uriProfileImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddEvent.this,"Success",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("AddEvent",e.getMessage());
                        Toast.makeText(AddEvent.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        if(flag == 0)
            mStartDateVIew.setText(currentDateString);
        else
            mEndDateVIew.setText(currentDateString);
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
}
