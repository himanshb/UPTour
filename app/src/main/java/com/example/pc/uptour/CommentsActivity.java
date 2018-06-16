package com.example.pc.uptour;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyHashAdapter;
import com.example.pc.uptour.classes.CommentStructure;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("comments");

    ListView commentList;
    HashMap<String,String> comment=new HashMap<>();
    MyHashAdapter myHashAdapter;

    CommentStructure commentStructure;
    EditText commentTextView;
    String placeType;
    String placeId;
    String userID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentList=findViewById(R.id.commentsList);

        //for reading the current user details
        user= FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getDisplayName();

        placeId= this.getIntent().getExtras().getString("place_id");
        placeType= this.getIntent().getExtras().getString("place_type");
        commentTextView=findViewById(R.id.commentTextView);
        commentStructure=new CommentStructure(userID,placeId,placeType);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comment.clear();
                for(DataSnapshot dataSnapshot_i: dataSnapshot.child(commentStructure.getType()).child(commentStructure.getPlaceId()).getChildren())
                {
                    String key=dataSnapshot_i.getKey();
                    String commentText=dataSnapshot.child(commentStructure.getType()).child(commentStructure.getPlaceId()).child(key).child("comment_text").getValue(String.class);
                    comment.put(key,commentText);
                    myHashAdapter=new MyHashAdapter(comment);
                    commentList.setAdapter(myHashAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }


    public void getComments()
    {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comment.clear();
                for(DataSnapshot dataSnapshot_i: dataSnapshot.child(commentStructure.getType()).child(commentStructure.getPlaceId()).getChildren())
                {
                    String key=dataSnapshot_i.getKey();
                    String commentText=dataSnapshot.child(commentStructure.getType()).child(commentStructure.getPlaceId()).child(key).child("comment_text").getValue(String.class);
                    comment.put(key,commentText);
                    myHashAdapter=new MyHashAdapter(comment);
                    commentList.setAdapter(myHashAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onClick(View view) {

        commentStructure.setCommentText(commentTextView.getText().toString());

        DatabaseReference typeRef=myRef.child(commentStructure.getType());
        DatabaseReference typeKey=typeRef.child(commentStructure.getPlaceId());
        DatabaseReference userID=typeKey.child(commentStructure.getUserId());
        DatabaseReference commentRef=userID.child("comment_text");

        commentRef.setValue(commentStructure.getCommentText());
        Toast.makeText(CommentsActivity.this, "Inserted", Toast.LENGTH_SHORT).show();

        //for comment retrieval
        getComments();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}
