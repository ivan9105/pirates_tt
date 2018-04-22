package pirates;

public interface JackSparrowHelper {
    /**
     * @param pathToPrices     path to file with rum providers sources.csv
     * @param numberOfGallons  requested number of gallons
     * @return <tt>Purchases</tt> result
     */
    Purchases helpJackSparrow(String pathToPrices, int numberOfGallons);
}
