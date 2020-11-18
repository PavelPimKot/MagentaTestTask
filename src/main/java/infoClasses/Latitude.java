package infoClasses;
import exceptionClasses.LatitudeMeasureException;

import java.io.Serializable;

public class Latitude implements Serializable {
    private double latitudeValue;

    public Latitude( double latitudeValue) throws LatitudeMeasureException {
        setLatitudeValue(latitudeValue);
    }
    public Latitude(){}

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) throws LatitudeMeasureException {
        if(latitudeValue<-90 || latitudeValue >90){
            throw  new LatitudeMeasureException();
        }
        this.latitudeValue = latitudeValue;
    }

    @Override
    public String toString() {
        return "Latitude:"
                + latitudeValue +
                '°';
    }
}
