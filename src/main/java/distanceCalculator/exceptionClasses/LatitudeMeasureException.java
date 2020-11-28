package distanceCalculator.exceptionClasses;

/**
 * @author PavelPimkot
 * @version 1.0
 * Class used to denote except situations which can arise in City and Distance classes
 * This class is used to warn about the situation when latitude is not in its range
 */
public class LatitudeMeasureException extends Throwable {
    @Override
    public String toString() {
        return "Incorrect latitude,values are in the range from -90 to 90";
    }
}
