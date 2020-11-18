package infoClasses;

import exceptionClasses.LongitudeMeasureException;

import java.io.Serializable;

public class Longitude implements Serializable {
    private double longitudeValue;

    public Longitude( double longitudeValue)throws LongitudeMeasureException  {
        setLongitudeValue(longitudeValue);
    }

    public Longitude() {}

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) throws LongitudeMeasureException {
        if( longitudeValue <-180 || longitudeValue > 180){
            throw new LongitudeMeasureException();
        }
        this.longitudeValue = longitudeValue;
    }

    @Override
    public String toString() {
        return "Longitude:"
                + longitudeValue +
                '°';
    }
}
