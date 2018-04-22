package pirates;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class JackSparrowHelperImplTest {
    @Test
    public void baseLogicTest() {
        checkHelpJackSparrowGoodResult("sources.csv", 750, 51.82666666666667, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Hector Barbossa", 300));
            put(2, getTestPurchase("Davy Jones", 100));
            put(3, getTestPurchase("Sao Feng", 60));
            put(4, getTestPurchase("Kraken", 200));
            put(5, getTestPurchase("Black Beard", 40));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 500, 50.7, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Hector Barbossa", 300));
            put(2, getTestPurchase("Davy Jones", 100));
            put(3, getTestPurchase("Sao Feng", 50));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 350, 50.42857142857143, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Hector Barbossa", 300));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 300, 50.5, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Hector Barbossa", 300));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 250, 50.4, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Hector Barbossa", 200));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 200, 50.5, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Hector Barbossa", 200));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 150, 50.666666666666664, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Davy Jones", 100));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 100, 51.0, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
            put(1, getTestPurchase("Sao Feng", 50));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 50, 50.0, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 50));
        }});

        checkHelpJackSparrowGoodResult("sources.csv", 10, 50.0, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("Mulroy", 10));
        }});
    }

    @Test
    public void checkValidationTest() {
        checkHelpJackSparrowBadResult("sources.csv", -1, "Number of gallons must be more then 0", true);
        checkHelpJackSparrowBadResult("sources.csv", 0, "Number of gallons must be more then 0", true);
        checkHelpJackSparrowBadResult("", 1, "Path to Prices can not be empty", true);
        checkHelpJackSparrowBadResult(null, 1, "Path to Prices can not be empty", true);
        checkHelpJackSparrowBadResult("nonexistentFile.csv", 1, "File 'nonexistentFile.csv' not found", true);
        checkHelpJackSparrowBadResult("withoutExtensionSources", 1, "", false);
        checkHelpJackSparrowBadResult("noCsvFile.sql", 1, "Row number: 1, error: 'Incorrect column count'", true);
        checkHelpJackSparrowBadResult("sources.csv", 1000000, "Number of gallons '1000000' too much, max size for request '910'", true);
        checkHelpJackSparrowBadResult("impossibleToFindAmount.csv", 45, "It is impossible to find the right number of gallons", true);
        checkHelpJackSparrowBadResult("withoutHeaderSources.csv", 40, "", false);
        checkHelpJackSparrowBadResult("duplicateNamesSources.csv", 40, "Row number: 6, error: 'Name 'Black Beard' already exists'", true);
    }

    @Test
    public void rangeDataTest() {
        checkHelpJackSparrowBadResult("soBigSources.csv", 2, "", false);
        checkHelpJackSparrowGoodResult("fillPurchaseByMinSizeOverRequest.csv", 100, 3, new LinkedHashMap<Integer, Purchase>() {{
            put(0, getTestPurchase("China", 200));
        }});
    }

    private void checkHelpJackSparrowGoodResult(String filePath, int numberOfGallons, double averagePrice, LinkedHashMap<Integer, Purchase> checkResultMap) {
        JackSparrowHelper helper = new JackSparrowHelperImpl();
        Purchases purchases = helper.helpJackSparrow(filePath, numberOfGallons);
        Assert.assertEquals(String.format("Check average fail, request %s, sources.csv",
                numberOfGallons), purchases.calculateAveragePrice(), averagePrice, 0d);
        Assert.assertEquals(String.format("Check purchases size fail, request %s, sources.csv",
                numberOfGallons), purchases.getPurchases().size(), checkResultMap.size(), 0);
        Purchase[] purchasesArr = convertToArray(purchases.getPurchases());

        for (int i = 0; i < checkResultMap.size(); i++) {
            Assert.assertEquals(String.format("Check purchases set[%s] source name fail, request %s, sources.csv", i,
                    numberOfGallons), purchasesArr[i].getSourceName(), checkResultMap.get(i).getSourceName());
            Assert.assertEquals(String.format("Check purchases set[%s] number of gallons fail, request %s, sources.csv",
                    i, numberOfGallons), purchasesArr[i].getNumberOfGallons(), checkResultMap.get(i).getNumberOfGallons(), 0);
        }
    }

    private void checkHelpJackSparrowBadResult(String filePath, int numberOfGallons, String errorMsg, boolean actual) {
        JackSparrowHelper helper = new JackSparrowHelperImpl();
        Purchases purchases = helper.helpJackSparrow(filePath, numberOfGallons);
        Assert.assertEquals(String.format("Check error '%s' fail", errorMsg), isNotEmpty(purchases.getError())
                && StringUtils.equals(errorMsg, purchases.getError()), actual);
    }

    private Purchase[] convertToArray(Set<Purchase> purchases) {
        Purchase[] res = new Purchase[purchases.size()];
        Object[] objArr = purchases.toArray();
        for (int i = 0; i < objArr.length; i++) {
            res[i] = (Purchase) objArr[i];
        }
        return res;
    }

    private Purchase getTestPurchase(String sourceName, Integer numberOfGallons) {
        return new Purchase(sourceName, numberOfGallons, -1d, -1, -1);
    }
}
