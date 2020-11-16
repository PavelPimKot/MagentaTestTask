package infoClasses;

import java.io.Serializable;

public class Longitude implements Serializable {
    private double longitudeValue;

    public Longitude( double longitudeValue){
        this.longitudeValue = longitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    @Override
    public String toString() {
        return "Longitude:"
                + longitudeValue +
                '°';
    }
}
