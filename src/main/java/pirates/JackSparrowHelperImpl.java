package pirates;

import pirates.exception.PurchaseParseException;
import pirates.utils.calculator.PurchaseCalculator;
import pirates.utils.parser.CSVPurchaseParser;
import pirates.utils.parser.PurchaseParser;

import java.util.Set;

public class JackSparrowHelperImpl implements JackSparrowHelper {
    @Override
    public Purchases helpJackSparrow(String pathToPrices, int numberOfGallons) {
        if (numberOfGallons <= 0) {
            return new Purchases(numberOfGallons, "Number of gallons must be more then 0");
        }

        if (pathToPrices == null || pathToPrices.length() == 0) {
            return new Purchases(numberOfGallons, "Path to Prices can not be empty");
        }

        Set<Purchase> purchaseSet;
        PurchaseParser parser = new CSVPurchaseParser();
        try {
            purchaseSet = parser.parse(pathToPrices);
        } catch (PurchaseParseException ppe) {
            return new Purchases(numberOfGallons, ppe.getMessage());
        }

        Integer allNumberOfGallons = purchaseSet.stream()
                .map(Purchase::getNumberOfGallons)
                .reduce(Integer::sum).orElse(0);

        if (allNumberOfGallons < numberOfGallons) {
            return new Purchases(numberOfGallons, String.format("Number of gallons '%s' too much" +
                    ", max size for request '%s'", numberOfGallons, allNumberOfGallons));
        }

        Purchases res = new Purchases(numberOfGallons);
        PurchaseCalculator calculator = new PurchaseCalculator();
        Set<Purchase> calculatedPurchase = calculator.calculate(numberOfGallons, purchaseSet);

        int totalCount = 0;
        for (Purchase purchase : calculatedPurchase) {
            totalCount += purchase.getNumberOfGallons();
        }

        if (totalCount != numberOfGallons && totalCount < numberOfGallons) {
            return new Purchases(numberOfGallons, "It is impossible to find the right number of gallons");
        }

        res.setPurchases(calculatedPurchase);


        return res;
    }
}
