
package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.example.chat.Message;

public class GroupChat extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User u;
    MessageAdapter messageAdapter;
    List<Message> messages;
    RecyclerView rvMessage;
    EditText etMessage;
    ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_group_chat);

        init();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder= new AlertDialog.Builder (this);
        builder.setMessage ("Are You Sure want to exit")
                .setCancelable (false)
                .setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish ();
                       GroupChat.super.onBackPressed ();
                    }
                })
                .setNegativeButton ("No", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel ();
                    }
                });
        AlertDialog alertDialog=builder.create ();
        alertDialog.show ();



        //super.onBackPressed ();


    }

    private void init() {

        firebaseAuth =FirebaseAuth.getInstance ();
        firebaseDatabase=FirebaseDatabase.getInstance ();
        u = new User ();
        rvMessage= (RecyclerView)findViewById (R.id.iv_msg);
        etMessage=(EditText)findViewById (R.id.text_msg);
        imageButton=(ImageButton)findViewById (R.id.sent_btn);
        imageButton.setOnClickListener (this);
        messages= new ArrayList<> ();
    }

    @Override
    public void onClick(View v) {
//        Log.d ("TEST1"
//                , "onClick: "+TextUtils.isEmpty (etMessage.getText ().toString ()));

        if (!TextUtils.isEmpty (etMessage.getText ().toString ())){
            Message message=new Message (etMessage.getText ().toString (),u.getFullName ());
            etMessage.setText ("");
            databaseReference.push ().setValue (message);
        }
        else{
            Toast.makeText (getApplicationContext (),"You can't send blank message",Toast.LENGTH_SHORT).show ();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu (menu);
        getMenuInflater ().inflate (R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId ()== R.id.menuL){
            firebaseAuth.signOut ();
            finish ();
            startActivity (new Intent (GroupChat.this,MainActivity.class));
        }
        return super.onOptionsItemSelected (item);
    }

    protected void onStart() {
        super.onStart ();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser ();
        u.setUid (currentUser.getUid ());
        u.setEmail (currentUser.getEmail ());
        firebaseDatabase.getReference ("User").child (currentUser.getUid ()).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                u =snapshot.getValue (User.class);
                u.setUid (currentUser.getUid ());
                AllMethods.name=u.getFullName ();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });

        databaseReference = firebaseDatabase.getReference ("messages");

        databaseReference.addChildEventListener (new ChildEventListener () {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren ()) {
                    Message message=dataSnapshot.getValue(Message.class);//(Message)
                    message.setKey(dataSnapshot.getKey ());
                    messages.add (message);
                    displayMessage(messages);
                }

           // }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message= snapshot.getValue(Message.class);//(Message)

                message.setKey(snapshot.getKey ());
                List<Message> newMessage= new ArrayList<Message> ();
                for(Message m: messages){
                    if (m.getKey().equals(message.getKey())){
                        newMessage.add (message);

                    }
                    else{
                        newMessage.add (m);
                    }
                }

                messages = newMessage;
                displayMessage (messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Message message=snapshot.getValue (Message.class);
                message.setKey(snapshot.getKey ());
                List<Message> newMessage= new ArrayList<Message> ();
                for(Message m: messages){
                    if (!m.getKey().equals(message.getKey())){
                        newMessage.add (m);

                    }
//                    messages = newMessage;
//                    displayMessage (messages);

                }
                messages = newMessage;
                displayMessage (messages);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }
     protected  void onResume() {

         super.onResume ();
         messages=new ArrayList<> ();
     }



    private void displayMessage(List<Message> messages) {

        rvMessage.setLayoutManager (new LinearLayoutManager (GroupChat.this));
        messageAdapter= new MessageAdapter (GroupChat.this,messages,databaseReference);
        rvMessage.setAdapter (messageAdapter);
    }


}