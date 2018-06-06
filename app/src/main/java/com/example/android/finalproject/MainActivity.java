package com.example.android.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int CHOOSE_IMAGE =  2;
    public static final int RC_SIGN_IN = 1;
    private ImageView mProfileImageView;
    private TextView mProfileNameTextView;
    private TextView mAddPostTextView;
    private TextView mAddEventTextView;
    private ProgressBar mProgressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Uri uriProfileImage;
    private  String profileImageUrl;
    private ProgressDialog dialog;

    //Firebase Instances
    private ListView listView;
    private MessageAdapter messageAdapter;
    private ChildEventListener childEventListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    private String mUsername;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mAddPostTextView = findViewById(R.id.add_post);
        mAddEventTextView = findViewById(R.id.add_event);
        listView = findViewById(R.id.listView);
        List<EventList> friendlyMessages = new ArrayList<EventList>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference =  firebaseDatabase.getReference().child("message");
        dialog = new ProgressDialog(this);
        messageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        listView.setAdapter(messageAdapter);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                }
                else {
                    StartSigningActivity();
                }
            }
        };
        mAddEventTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpToAddEvent();
            }
        });
    }

    private void StartSigningActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.loginicon)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    private void JumpToAddEvent(){

        Intent intent = new Intent(MainActivity.this,AddEvent.class);
        startActivity(intent);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        dialog.setMessage("Loadinng");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        messageAdapter.clear();
        detachDatabaseReadlistener();
    }

    private void attachDatabaseReadListener(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Toast.makeText(MainActivity.this,"Events Uodated",Toast.LENGTH_SHORT).show();
                    EventList friendlyMessage = dataSnapshot.getValue(EventList.class);
                    messageAdapter.add(friendlyMessage);
                    dialog.dismiss();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }

    }

    private void detachDatabaseReadlistener(){
        if(childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this,"Signed In Cancled",Toast.LENGTH_SHORT);
                finish();
            }
        }
    }
}

