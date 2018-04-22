package pirates.utils.parser;

import pirates.Purchase;
import pirates.exception.PurchaseParseException;

import java.util.Set;

public interface PurchaseParser {
    /**
     * Read Purchase {@link Purchase} from file
     * @param pathToFile path to file with rum providers target file
     * @return set of Purchase
     * @throws PurchaseParseException
     */
    Set<Purchase> parse(String pathToFile) throws PurchaseParseException;
}