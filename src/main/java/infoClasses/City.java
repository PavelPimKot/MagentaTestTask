package infoClasses;

import exceptionClasses.LatitudeMeasureException;
import exceptionClasses.LongitudeMeasureException;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "cities")
public class City implements Serializable {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude",nullable = false)
    private double latitude;

    @Column(name = "longitude",nullable = false)
    private double longitude;

    public City( String name ,double latitude,double longitude) throws LatitudeMeasureException, LongitudeMeasureException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.name = name;
    }

    public City(){}

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

    public void setLatitude(double latitude) throws LatitudeMeasureException {
        if(latitude<-90 || latitude >90){
            throw  new LatitudeMeasureException();
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) throws LongitudeMeasureException {
        if( longitude <-180 || longitude > 180){
            throw new LongitudeMeasureException();
        }
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name+ '(' + " Latitude:"+ latitude + '°'+" , "+" Longitude:"+longitude+'°'+')';
    }
}
