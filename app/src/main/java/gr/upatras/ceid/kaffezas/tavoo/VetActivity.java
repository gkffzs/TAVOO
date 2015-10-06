package gr.upatras.ceid.kaffezas.tavoo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

// -------------------------------------------------------------------------------------------------
// This activity displays two tabs with VetFirstActivity and VetSecondActivity. It is the landing --
// activity when the vet button is pressed from MainActivity. --------------------------------------
// -------------------------------------------------------------------------------------------------
public class VetActivity extends TabActivity { // --------------------------------------------------
    // Declaration of the necessary TabHost. -------------------------------------------------------
    TabHost tabHost;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tabHost = getTabHost();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("VetFirstActivity");
        tab1.setIndicator("Χάρτης");
        tab1.setContent(new Intent(this, VetFirstActivity.class));

        TabHost.TabSpec tab2 = tabHost.newTabSpec("VetSecondActivity");
        tab2.setIndicator("Κατάλογος");
        tab2.setContent(new Intent(this, VetSecondActivity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}