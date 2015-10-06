package gr.upatras.ceid.kaffezas.tavoo;

// -------------------------------------------------------------------------------------------------
// This class is used to represent a vet, with all necessary information and related methods. ------
// Variables' and methods' names are self-explanatory. It was created for better data handling. ----
// -------------------------------------------------------------------------------------------------
public class Vet { // ------------------------------------------------------------------------------

    // Declaration of the required variables of the class. -----------------------------------------
    private String name;
    private String address;
    private String telephone;
    private double lat;
    private double lng;
    private int vetID;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public int getVetID() {
        return vetID;
    }

    public void setVetID(int vetID) {
        this.vetID = vetID;
    }
}