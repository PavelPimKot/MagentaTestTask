package infoClasses;

import java.io.Serializable;

public class Distance implements Serializable {
    private City fromCity;
    private City toCity;
    private double distance;
    private final static double EARTH_RAD = 6371;//km

    private Distance( City fromCity, City toCity, double distance){
        this.toCity = toCity;

        this.fromCity = fromCity;

        this.distance = distance;
    }

    public Distance(){}

    public static Distance getDistanceBetweenStraight(City fromCity, City toCity){
        double dLat = Math.toRadians(toCity.getLatitude().getLatitudeValue()-fromCity.getLatitude().getLatitudeValue());
        double dLng = Math.toRadians(toCity.getLongitude().getLongitudeValue()-fromCity.getLongitude().getLongitudeValue());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(fromCity.getLatitude().getLatitudeValue())) *
                        Math.cos(Math.toRadians(toCity.getLatitude().getLatitudeValue())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double distance = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance *= EARTH_RAD;
        return new Distance( fromCity, toCity, distance);
    }

    public void setFromCity(City fromCity) {
        this.fromCity = fromCity;
    }

    public void setToCity(City toCity) {
        this.toCity = toCity;
    }

    public City getFromCity() {
        return fromCity;
    }

    public City getToCity() {
        return toCity;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "fromCity=" + fromCity +
                ", toCity=" + toCity +
                ", distance=" + distance +
                '}';
    }
}
