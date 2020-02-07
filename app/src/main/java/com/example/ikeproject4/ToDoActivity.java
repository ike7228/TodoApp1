package com.example.ikeproject4;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;


import java.util.ArrayList;


public class ToDoActivity extends AppCompatActivity implements ListView.OnItemLongClickListener{

    public FirebaseUser user;
    public String uid;

    public FirebaseAuth mAuth;

    public FirebaseDatabase database;
    public DatabaseReference reference;

    public CustomAdapter mCustomAdapter;

    public ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        user= FirebaseAuth.getInstance().getCurrentUser();

        uid = user.getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        mListView = (ListView) findViewById(R.id.list_view);

        mCustomAdapter = new CustomAdapter(getApplicationContext(),R.layout.card_view, new ArrayList<ToDoData>());
        mListView.setAdapter(mCustomAdapter);

        mListView.setOnItemLongClickListener(this);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot datasnapshot, @Nullable String s){
                ToDoData toDoData = datasnapshot.getValue(ToDoData.class);
                mCustomAdapter.add(toDoData);
                mCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,@Nullable String s){

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){
                Log.d("ToDoActivity", "onChildRemoved:"+ dataSnapshot.getKey());
                ToDoData result = dataSnapshot.getValue(ToDoData.class);
                if(result == null)return;

                ToDoData item = mCustomAdapter.getToDoDataKey(result.getFirebaseKey());

                mCustomAdapter.remove(item);
                mCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,@Nullable String s){

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });


            }

            public void addButton(View v){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);

            }
            @Override
            public boolean onItemLongClick(AdapterView<?>parent,View view , final int position, long id){
        final ToDoData toDoData = mCustomAdapter.getItem(position);
        uid = user.getUid();

        new AlertDialog.Builder(this)
                .setTitle("Done?")
                .setMessage("この項目を完了しましたか？")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.child(toDoData.getFirebaseKey()).removeValue();
                        mCustomAdapter.remove(toDoData);
                    }
                })
                        .setNegativeButton("No",null)
                        .show();
        return false;
            }
            public  void logout(View v){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent (ToDoActivity.this,LoginActivity.class);
        intent.putExtra("check", true);
        startActivity(intent);
        finish();
            }


}
