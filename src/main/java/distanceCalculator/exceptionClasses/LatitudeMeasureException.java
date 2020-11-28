package distanceCalculator.exceptionClasses;

public class LatitudeMeasureException extends Throwable {
    @Override
    public String toString() {
        return "Incorrect latitude,values are in the range from -90 to 90";
    }
}
