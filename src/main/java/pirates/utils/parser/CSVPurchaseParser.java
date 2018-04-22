package pirates.utils.parser;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import pirates.Purchase;
import pirates.exception.PurchaseParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class CSVPurchaseParser implements PurchaseParser {
    private static final String SOURCE_NAME_COLUMN_HEADER = "Source Name";
    private static final int COLUMNS_COUNT = 5;

    @Override
    public Set<Purchase> parse(String pathToFile) throws PurchaseParseException {
        Set<Purchase> res = new HashSet<>();
        Set<String> sourceNames = new HashSet<>();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pathToFile);
            if (inputStream == null) {
                throw new PurchaseParseException(format("File '%s' not found", pathToFile));
            }

            CSVIterator iterator = new CSVIterator(new CSVReader(new InputStreamReader(inputStream)));
            int rowNumber = 1;
            while (iterator.hasNext()) {
                String[] line = iterator.next();
                //empty line
                if (line.length == 0) {
                    continue;
                }

                //bad csv format, csv must be has comma delimiter, check other symbols
                if (line.length == 1) {
                    line = line[0].split("[^\\d^\\w^\\s^,^.]");
                }

                //it's header
                if (SOURCE_NAME_COLUMN_HEADER.equals(line[0])) {
                    continue;
                }

                if (line.length != COLUMNS_COUNT) {
                    throw new PurchaseParseException(lineValidationErrorMsg("Incorrect column count", rowNumber));
                }

                String sourceName = line[0];

                if (sourceNames.contains(sourceName)) {
                    throw new PurchaseParseException(lineValidationErrorMsg(format("Name '%s' already exists", sourceName), rowNumber));
                }

                Integer numberOfGallons = parseInteger(line[1], 2, rowNumber);
                Double priceOfGallon = parseDouble(line[2], 3, rowNumber);
                Integer minSize = parseInteger(line[3], 4, rowNumber);
                Integer stepSize = parseInteger(line[4], 5, rowNumber);

                sourceNames.add(sourceName);

                res.add(new Purchase(sourceName, numberOfGallons, priceOfGallon, minSize, stepSize));

                rowNumber++;
            }
        } catch (IOException e) {
            throw new PurchaseParseException(format("Read file '%s' error", pathToFile));
        }

        if (res.isEmpty()) {
            throw new PurchaseParseException("File is empty");
        }

        return res;
    }

    private Integer parseInteger(String strValue, int columnNumber, int rowNumber) throws PurchaseParseException {
        try {
            Integer res = Integer.valueOf(strValue);
            checkNegativeValue(columnNumber, rowNumber, res);
            return res;
        } catch (NumberFormatException nfe) {
            throw new PurchaseParseException(columnValidationErrorMsg(columnNumber, rowNumber));
        }
    }

    private Double parseDouble(String strValue, int columnNumber, int rowNumber) throws PurchaseParseException {
        try {
            Double res = Double.valueOf(strValue);
            checkNegativeValue(columnNumber, rowNumber, res);
            return res;
        } catch (NumberFormatException nfe) {
            throw new PurchaseParseException(columnValidationErrorMsg(columnNumber, rowNumber));
        }
    }

    private void checkNegativeValue(int columnNumber, int rowNumber, Number res) throws PurchaseParseException {
        if (res.intValue() <= 0) {
            throw new PurchaseParseException(lineValidationErrorMsg(format("Number value must be more than 0" +
                            ", column: %s", columnNumber), rowNumber));
        }
    }

    private String lineValidationErrorMsg(String msg, int rowNumber) {
        return format("Row number: %s, error: '%s'", rowNumber, msg);
    }

    private String columnValidationErrorMsg(int columnNumber, int rowNumber) {
        return lineValidationErrorMsg(format("Can not parse column: %s", columnNumber), rowNumber);
    }
}
