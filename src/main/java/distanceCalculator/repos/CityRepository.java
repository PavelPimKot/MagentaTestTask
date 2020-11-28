package distanceCalculator.repos;

import distanceCalculator.infoClasses.City;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<City, Integer> {
    List<City> findAll();

    City findByName(String name);
}
