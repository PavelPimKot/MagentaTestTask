package infoClasses;

import java.io.Serializable;

public class City implements Serializable {

    private String name;
    private Latitude latitude;
    private Longitude longitude;

    public City( String name ,Latitude latitude,Longitude longitude){
        this.latitude  = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Latitude getLatitude() {
        return latitude;
    }

    public Longitude getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Latitude latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Longitude longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name+ '('+ latitude.toString() + " , "+longitude.toString()+')';
    }
}
