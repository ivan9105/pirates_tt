package pirates;

import java.util.HashSet;
import java.util.Set;

public class Purchases {
    private final int requestedNumberOfGallons;
    private Set<Purchase> purchases;
    private String error;

    public Purchases(int requestedNumberOfGallons) {
        this.requestedNumberOfGallons = requestedNumberOfGallons;
        this.purchases = new HashSet<>();
    }

    public Purchases(int requestedNumberOfGallons, String error) {
        this.requestedNumberOfGallons = requestedNumberOfGallons;
        this.error = error;
        this.purchases = new HashSet<>();
    }

    public Double calculateAveragePrice() {
        if (purchases.size() == 0) {
            return 0d;
        }

        Purchase firstPurchase = (Purchase) purchases.toArray()[0];
        if (purchases.size() == 1 && firstPurchase.getNumberOfGallons() != requestedNumberOfGallons) {
            return firstPurchase.getPriceOfGallon();
        }

        double totalSum = 0;
        for (Purchase purchase : getPurchases()) {
            totalSum += purchase.getTotalPrice();
        }
        return totalSum / getRequestedNumberOfGallons();
    }

    public Integer getRequestedNumberOfGallons() {
        return requestedNumberOfGallons;
    }

    public Set<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        this.purchases = purchases;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
