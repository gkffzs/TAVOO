package gr.upatras.ceid.kaffezas.tavoo;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This class is used to parse the data that come in XML format and are about play locations. ------
// Returns a List of PlayLocation items. -----------------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class PlayLocationXMLParser { // ------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Method that takes a String (a raw XML file) as input and returns a List of PlayLocation -----
    // items. --------------------------------------------------------------------------------------
    public static List<PlayLocation> parseFeed(String content) {
        try {
            List<PlayLocation> playLocationsList = new ArrayList<>();
            PlayLocation playLocation = null;
            boolean inDataItemTag = false;
            String currentTagName = "";

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("location")) {
                            inDataItemTag = true;
                            playLocation = new PlayLocation();
                            playLocationsList.add(playLocation);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("location")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag) {
                            switch (currentTagName) {
                                case "id":
                                    playLocation.setPlayLocationID(Integer.parseInt(parser.getText()));
                                    break;
                                case "name":
                                    playLocation.setName(parser.getText());
                                    break;
                                case "address":
                                    playLocation.setAddress(parser.getText());
                                    break;
                                case "lat":
                                    playLocation.setLat(Double.parseDouble(parser.getText()));
                                    break;
                                case "lng":
                                    playLocation.setLng(Double.parseDouble(parser.getText()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }

            for (PlayLocation temp : playLocationsList) {
                Log.d("PlayLocationXMLParser", temp.getName() + " was parsed successfully");
            }

            return playLocationsList;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}