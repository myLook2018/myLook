package com.mylook.mylook.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mylook.mylook.home.MyLookActivity;
import com.mylook.mylook.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Sesion currentSesion;
    private static final String TAG = "Main Activity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSession();
        Intent incomingIntent = getIntent();
        if(incomingIntent.getExtras() != null){
            Log.e(TAG, "Intent as extras");
            if(incomingIntent.hasExtra("pushnotification"))
                Log.e(TAG,incomingIntent.getStringExtra("pushnotification"));
            if(incomingIntent.hasExtra("requestId")){
                Log.e(TAG, incomingIntent.getStringExtra("requestId"));
            }
            for(String key : incomingIntent.getExtras().keySet()){
                Log.e(TAG,"Key: "+key+" - Value:"+incomingIntent.getExtras().get(key).toString());
            }

        }
        if(currentSesion!=null) {
            currentSesion.initializeElements().addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Intent intent= new Intent(MainActivity.this, MyLookActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            try {
               finish();
            }catch (Throwable e){
                Log.e("current Sesion is null ", e.getMessage() );

            }
        }
    }
    private void getSession(){
        currentSesion = Sesion.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
