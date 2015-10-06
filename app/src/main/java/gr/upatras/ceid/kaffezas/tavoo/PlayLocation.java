package gr.upatras.ceid.kaffezas.tavoo;

// -------------------------------------------------------------------------------------------------
// This class is used to represent a play location, with all necessary information and related -----
// methods. Variables' and methods' names are self-explanatory. It was created for better data -----
// handling. ---------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class PlayLocation { // ---------------------------------------------------------------------

    // Declaration of the required variables of the class. -----------------------------------------
    private int playLocationID;
    private String name;
    private String address;
    private double lat;
    private double lng;

    public int getPlayLocationID() {
        return playLocationID;
    }

    public void setPlayLocationID(int playLocationID) {
        this.playLocationID = playLocationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}