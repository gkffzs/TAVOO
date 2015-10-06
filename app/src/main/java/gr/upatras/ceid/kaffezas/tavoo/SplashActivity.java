package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// -------------------------------------------------------------------------------------------------
// This activity is used to display the initial splash screen when the application opens. ----------
// Maybe it could be used to fetch several files before they're requested from the user. -----------
// -------------------------------------------------------------------------------------------------
public class SplashActivity extends Activity { // --------------------------------------------------

    // Declaration of the variable that specifies the time (in milliseconds). ----------------------
    int SPLASH_TIME_OUT = 3000;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}