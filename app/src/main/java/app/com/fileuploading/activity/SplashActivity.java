package app.com.fileuploading.activity;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import app.com.fileuploading.R;

public class SplashActivity extends AppCompatActivity {
    private Context mContext;
    private FirebaseAuth auth;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initUI();
        moveToNextScreen();
    }

    private void initUI() {
        mContext = getApplicationContext();
    }

    private void moveToNextScreen() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                if (auth.getCurrentUser() != null  ) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
                else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}