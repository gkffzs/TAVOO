package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

// -------------------------------------------------------------------------------------------------
// This adapter is used to specify the style of the items in the vets' list of VetSecondActivity. --
// -------------------------------------------------------------------------------------------------
public class VetAdapter extends ArrayAdapter<Vet> { // ---------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;

    // Declaration of a couple of variables that are required for the adapter. ---------------------
    private Context context;
    private List<Vet> vetList;

    // ---------------------------------------------------------------------------------------------
    // Constructor method of the adapter. ----------------------------------------------------------
    public VetAdapter(Context context, int resource, List<Vet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.vetList = objects;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that informs each item of the list with the appropriate information. -----------------
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_vet, parent, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);

        final Vet vet = vetList.get(position);
        TextView tv_name = (TextView) view.findViewById(R.id.vet_sec_item_name);
        tv_name.setText(vet.getName());
        TextView tv_address = (TextView) view.findViewById(R.id.vet_sec_item_address);
        tv_address.setText(vet.getAddress());
        ImageButton img_btn = (ImageButton) view.findViewById(R.id.vet_sec_item_call_button);
        final String phone_number = "tel:" + vet.getTelephone();
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppServices.loggingAction(loggedInUserID, context.getString(R.string.activ_vet_sec_tag), context.getString(R.string.act_vet_call_tag), vet.getVetID());
                Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone_number));
                context.startActivity(call_intent);
            }
        });

        return view;
    }
}