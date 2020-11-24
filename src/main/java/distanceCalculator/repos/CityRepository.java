package distanceCalculator.repos;

import distanceCalculator.infoClasses.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository  extends CrudRepository<City, Integer> {
}
