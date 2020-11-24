package distanceCalculator.repos;

import distanceCalculator.infoClasses.Distance;
import org.springframework.data.repository.CrudRepository;

public interface DistanceRepository  extends CrudRepository<Distance, Integer> {
}
