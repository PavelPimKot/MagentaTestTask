package distanceCalculator.exceptionClasses;

/**
 * @author PavelPimkot
 * @version 1.0
 * Class used to denote except situations which can arise in City and Distance classes
 * This class is used to warn about the situation when longitude is not in its range
 */
public class LongitudeMeasureException extends Throwable {
    @Override
    public String toString() {
        return "Incorrect longitude,values are in the range from -180 to 180";
    }
}
