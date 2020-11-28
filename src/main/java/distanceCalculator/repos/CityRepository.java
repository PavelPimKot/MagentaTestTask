package distanceCalculator.repos;

import distanceCalculator.infoClasses.City;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author PavelPimkot
 * @version 1.0
 * Interface which is used as abstraction of a table of cities from a database
 * Used to save, serf information in city table
 */
public interface CityRepository extends CrudRepository<City, Integer> {
    List<City> findAll();

    City findByName(String name);
}
