package project.weatherapp;

/**
 * Created by Lukas on 21/06/2015.
 */
public enum UnitSystem {
    METRIC, IMPERIAL;

    public static UnitSystem StringToEnum (String myEnumString) {
        try {
            return valueOf(myEnumString);
        } catch (Exception ex) {
            // For error cases
            return METRIC;
        }
    }
}
