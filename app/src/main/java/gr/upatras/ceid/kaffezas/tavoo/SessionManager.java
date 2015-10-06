package gr.upatras.ceid.kaffezas.tavoo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This class is responsible for the communication between the application and the server, for the -
// login and registration functionality. It either sends a POST or a GET request, and passes the ---
// server response to the rest of the activities as a JSONObject. ----------------------------------
// -------------------------------------------------------------------------------------------------
public class SessionManager { // -------------------------------------------------------------------

    // Declaration of variables that are used in the method below. ---------------------------------
    static InputStream inputStream = null;
    static JSONObject jObject = null;
    static String rawJSON = "";

    // ---------------------------------------------------------------------------------------------
    // Method that takes a URL as input, a list of parameters to be passed to it and the type of ---
    // request that is should make to the server. It then returns its response. --------------------
    public JSONObject sendRequest(String givenURL, String method, List<NameValuePair> parameters) {
        try {
            DefaultHttpClient httpClient;
            HttpResponse httpResponse;
            HttpEntity httpEntity;
            String finalURL;
            switch (method) {
                case "POST":
                    httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(givenURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                    httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    inputStream = httpEntity.getContent();
                    break;
                case "GET":
                    httpClient = new DefaultHttpClient();
                    String parametersString = URLEncodedUtils.format(parameters, "utf-8");
                    finalURL = givenURL + "?" + parametersString;
                    HttpGet httpGet = new HttpGet(finalURL);
                    httpResponse = httpClient.execute(httpGet);
                    httpEntity = httpResponse.getEntity();
                    inputStream = httpEntity.getContent();
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            rawJSON = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObject = new JSONObject(rawJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObject;
    }
}