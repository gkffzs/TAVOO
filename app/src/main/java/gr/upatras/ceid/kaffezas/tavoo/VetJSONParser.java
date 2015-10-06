package gr.upatras.ceid.kaffezas.tavoo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This class is used to parse the data that come in JSON format and are about vets. ---------------
// Returns a List of Vet items. --------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class VetJSONParser { // --------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Method that takes a String (a raw JSON file) as input and returns a List of Vet items. ------
    public static List<Vet> parseFeed(String content) throws JSONException {
        JSONArray array = new JSONArray(content);
        List<Vet> vetList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Vet vet = new Vet();
            vet.setName(obj.getString("name"));
            vet.setAddress(obj.getString("address"));
            vet.setTelephone(obj.getString("telephone"));
            vet.setLat(obj.getDouble("lat"));
            vet.setLng(obj.getDouble("lng"));
            vet.setVetID(obj.getInt("vetID"));
            vetList.add(vet);
        }
        return vetList;
    }
}