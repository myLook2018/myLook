package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.DiffusionMessage;

import java.util.ArrayList;
import java.util.List;

public class PremiumDiffusionFragment extends Fragment {

    private  String premiumUserId;
    private List<DiffusionMessage> oldMessages;
    MessagesRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;



    public PremiumDiffusionFragment() {
    }

    @SuppressLint("ValidFragment")
    public PremiumDiffusionFragment(String premiumUserId) {
        this.premiumUserId=premiumUserId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_diffusion, container, false);
        recyclerView=rootView.findViewById(R.id.premium_messages);
        getOldMessages();
        return rootView;
    }
    private void getOldMessages(){
        if (oldMessages == null || oldMessages.size() == 0){
            oldMessages = new ArrayList<DiffusionMessage>();
        }
        initRecyclerView();
        Log.e("UserId",premiumUserId);
        Log.e("topic", "topic_"+premiumUserId);
        FirebaseFirestore.getInstance().collection("diffusionMessages")
                .whereEqualTo("userId", premiumUserId)
                .whereEqualTo("topic", "topic_"+premiumUserId)
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
        adapter = new MessagesRecyclerViewAdapter(getActivity(), oldMessages,true);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(false);
        recyclerView.setLayoutManager(llm);

    }


}
