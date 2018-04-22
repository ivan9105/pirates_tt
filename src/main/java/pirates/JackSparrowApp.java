package pirates;

import static java.lang.String.format;

public class JackSparrowApp {
    private static final String FILE = "sources.csv";

    public static void main(String[] args) {
        JackSparrowHelper helper = new JackSparrowHelperImpl();
        Purchases purchases = helper.helpJackSparrow(FILE, 350);
        if (purchases.getError() != null) {
            System.out.println(format("Purchase not calculated, see error: %s", purchases.getError()));
            return;
        }

        System.out.println(format("Average price: %s", purchases.calculateAveragePrice()));
        System.out.println(purchases.getPurchases());
    }
}
