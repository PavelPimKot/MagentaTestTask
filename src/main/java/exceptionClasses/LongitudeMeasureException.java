package exceptionClasses;

public class LongitudeMeasureException extends  Throwable {
    @Override
    public String toString() {
        return "Incorrect longitude,values are in the range from -180 to 180";
    }
}
