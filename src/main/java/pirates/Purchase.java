package pirates;

import lombok.ToString;

@ToString
public class Purchase implements Cloneable {
    private String sourceName;
    private Integer numberOfGallons;
    private Double priceOfGallon;
    private Integer minSize;
    private Integer stepSize;

    public Purchase(String sourceName, Integer numberOfGallons, Double priceOfGallon, Integer minSize, Integer stepSize) {
        this.sourceName = sourceName;
        this.numberOfGallons = numberOfGallons;
        this.priceOfGallon = priceOfGallon;
        this.minSize = minSize;
        this.stepSize = stepSize;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(final String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getNumberOfGallons() {
        return numberOfGallons;
    }

    public void setNumberOfGallons(final Integer numberOfGallons) {
        this.numberOfGallons = numberOfGallons;
    }

    public Double getPriceOfGallon() {
        return priceOfGallon;
    }

    public void setPriceOfGallon(final Double priceOfGallon) {
        this.priceOfGallon = priceOfGallon;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public void setMinSize(Integer minSize) {
        this.minSize = minSize;
    }

    public Integer getStepSize() {
        return stepSize;
    }

    public void setStepSize(Integer stepSize) {
        this.stepSize = stepSize;
    }

    public Double getTotalPrice() {
        return getPriceOfGallon() * getNumberOfGallons();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
