package infoClasses;
import java.io.Serializable;

public class Latitude implements Serializable {
    private double latitudeValue;

    public Latitude( double latitudeValue){
        this.latitudeValue = latitudeValue;
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    @Override
    public String toString() {
        return "Latitude:"
                + latitudeValue +
                '°';
    }
}
