package distanceCalculator.repos;

import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface DistanceRepository extends CrudRepository<Distance, Integer> {
    List<Distance> findAllByFromCity(City fromCity);

    List<Distance> findAll();
}
