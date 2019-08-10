package com.mylook.mylook.session;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mylook.mylook.home.MyLookActivity;
import com.mylook.mylook.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Session currentSesion;
    private static final String TAG = "Main Activity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSession();
        if(currentSesion!=null) {
            Intent intent= new Intent(MainActivity.this, MyLookActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
        currentSesion = Session.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}