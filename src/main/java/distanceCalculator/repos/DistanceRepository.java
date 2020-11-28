package distanceCalculator.repos;

import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * @author PavelPimkot
 * @version 1.0
 * Interface which is used as abstraction of a table of distances from a database
 * Used to save, serf information in distance table
 */
public interface DistanceRepository extends CrudRepository<Distance, Integer> {
    List<Distance> findAllByFromCity(City fromCity);

    List<Distance> findAll();
}
