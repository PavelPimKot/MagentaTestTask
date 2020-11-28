package distanceCalculator.infoClasses;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import distanceCalculator.exceptionClasses.*;

/**
 * @author PavelPimkot
 * @version 1.0
 * Class which realizes cities in my project
 */
@Entity
@Table(name = "cities")
public class City implements Serializable {


    /**
     * It is using only in DataBase, to identify objects
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "latitude", nullable = false)
    private double latitude;


    @Column(name = "longitude", nullable = false)
    private double longitude;

    public City(String name, double latitude, double longitude) throws LatitudeMeasureException, LongitudeMeasureException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.name = name;
    }

    public City() {
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @throws LatitudeMeasureException When latitude is not in range
     */
    public void setLatitude(double latitude) throws LatitudeMeasureException {
        if (latitude < -90 || latitude > 90) {
            throw new LatitudeMeasureException();
        }
        this.latitude = latitude;
    }

    /**
     * @throws LongitudeMeasureException When latitude is not in range
     */
    public void setLongitude(double longitude) throws LongitudeMeasureException {
        if (longitude < -180 || longitude > 180) {
            throw new LongitudeMeasureException();
        }
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name + '(' + " Latitude:" + latitude + '°' + " , " + " Longitude:" + longitude + '°' + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Double.compare(city.latitude, latitude) == 0 &&
                Double.compare(city.longitude, longitude) == 0 &&
                Objects.equals(name, city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latitude, longitude);
    }
}
