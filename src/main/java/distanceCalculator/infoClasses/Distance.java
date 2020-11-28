package distanceCalculator.infoClasses;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "distances")
public class Distance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_City", nullable = false, referencedColumnName = "id")
    private City fromCity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_City", nullable = false, referencedColumnName = "id")
    private City toCity;

    @Column(name = "distance", nullable = false)
    private double distance;
    private final static double EARTH_RAD = 6371;//km

    public Distance(City fromCity, City toCity, double distance) {
        this.toCity = toCity;

        this.fromCity = fromCity;

        this.distance = distance;
    }

    public Distance() {
    }

    public static Distance getDistanceBetweenStraight(City fromCity, City toCity) {
        double dLat = Math.toRadians(toCity.getLatitude() - fromCity.getLatitude());
        double dLng = Math.toRadians(toCity.getLongitude() - fromCity.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(fromCity.getLatitude())) *
                        Math.cos(Math.toRadians(toCity.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double distance = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance *= EARTH_RAD;
        return new Distance(fromCity, toCity, distance);
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
