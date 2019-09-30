package com.mylook.mylook.premiumUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.DiffusionMessage;
import com.mylook.mylook.entities.Topic;
import com.mylook.mylook.recommend.AnswersRecyclerViewAdapter;
import com.mylook.mylook.recommend.RequestRecommendActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewDiffusionMessage extends Activity {

    private TextView newMessage;
    private Button sendMessage;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirebaseFirestore dB;
    private String userId = FirebaseAuth.getInstance().getUid();
    private List<DiffusionMessage> oldMessages;
    private String topic;
    MessagesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initElements();
    }

    private void initElements(){
        setContentView(R.layout.activity_premium_messages);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Difusiones");
        newMessage = findViewById(R.id.new_message_text);
        sendMessage = findViewById(R.id.send_new_message);
        recyclerView = findViewById(R.id.sent_premium_messages);
        this.dB = FirebaseFirestore.getInstance();

        sendMessage.setOnClickListener(v -> {
            sendMessage();
        });
        dB.collection("topics").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(v -> {
                    if(v.getDocuments().size() == 0){
                        Topic newTopic = new Topic(
                                "topic_"+FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                FirebaseAuth.getInstance().getUid(),
                                Timestamp.now());
                        dB.collection("topics").add(newTopic).addOnSuccessListener(newTopicTask -> {
                            topic = "topic_"+FirebaseAuth.getInstance().getUid();
                            getOldMessages();
                        });
                    } else {
                        topic = v.getDocuments().get(0).toObject(Topic.class).getTopic();
                        getOldMessages();
                    }
        });

    }

    private void getOldMessages(){
        if (oldMessages == null || oldMessages.size() == 0){
            oldMessages = new ArrayList<DiffusionMessage>();
        }
        initRecyclerView();
        Log.e("UserId",userId);
        Log.e("topic", topic);
        dB.collection("diffusionMessages")
                .whereEqualTo("userId", userId)
                .whereEqualTo("topic", topic)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(v ->{
            Log.e("NewDifusionMessage", "Documents: "+v.getDocuments().size());
           if (v.getDocuments().size() > 0){
               oldMessages.clear();
               for(DocumentSnapshot doc: v.getDocuments()){
                    oldMessages.add(doc.toObject(DiffusionMessage.class));
                    oldMessages.get(oldMessages.size() -1).setDocumentId(doc.getId());
               }
               adapter.notifyDataSetChanged();
           }
        });
    }

    private void initRecyclerView() {
        adapter = new MessagesRecyclerViewAdapter(NewDiffusionMessage.this, oldMessages);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setReverseLayout(true);
        recyclerView.setLayoutManager(llm);

    }

    private void sendMessage(){
        if(validateMessage()) {
            DiffusionMessage newDiffusion = new DiffusionMessage();
            newDiffusion.setTopic(topic);
            newDiffusion.setMessage(newMessage.getText().toString());
            newDiffusion.setUserId(FirebaseAuth.getInstance().getUid());
            newDiffusion.setCreationDate(Timestamp.now());



            dB.collection("diffusionMessages").add(newDiffusion)
                    .addOnSuccessListener(v -> {
                        newMessage.setText("");
                        getOldMessages();
                    })
                    .addOnFailureListener(v -> Log.e("ESHOR", v.getMessage()));
        }
    }

    private boolean validateMessage(){
        if(newMessage.getText().length() == 0){
            return false;
        }

        return true;
    }
}
