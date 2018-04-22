package pirates.utils.calculator;

import pirates.Purchase;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class PurchaseCalculator {
    public Set<Purchase> calculate(int numberOfGallons, Set<Purchase> purchaseSet) {
        Set<Purchase> res = new LinkedHashSet<>();

        //Check in end
        List<Purchase> minStepOverRequestList = purchaseSet
                .stream()
                .filter(p -> p.getMinSize() > numberOfGallons).collect(Collectors.toList());
        purchaseSet.removeIf(next -> next.getMinSize() > numberOfGallons);

        Double bestResult = Double.MAX_VALUE;
        if (purchaseSet.size() == 0) {
            fillPurchaseByMinSizeOverRequest(res, minStepOverRequestList, bestResult);
            return res;
        }
        //Greedy algorithm - approximately optimally

        List<List<Purchase>> resultList = executeTasks(numberOfGallons, purchaseSet);

        for (List<Purchase> result : resultList) {
            double totalSum = 0;
            int totalCount = 0;
            for (Purchase purchase : result) {
                totalSum += purchase.getTotalPrice();
                totalCount += purchase.getNumberOfGallons();
            }
            if (totalSum < bestResult && totalCount == numberOfGallons) {
                res.clear();
                res.addAll(result);
                bestResult = totalSum;
            }
        }

        //check purchase when minSize over request
        fillPurchaseByMinSizeOverRequest(res, minStepOverRequestList, bestResult);

        return res;
    }

    private void fillPurchaseByMinSizeOverRequest(Set<Purchase> res,
                                                  List<Purchase> minStepOverRequestList, Double bestResult) {
        for (Purchase purchase : minStepOverRequestList) {
            Purchase clone;
            try {
                clone = (Purchase) purchase.clone();
                clone.setNumberOfGallons(purchase.getMinSize());
                double totalSum = purchase.getTotalPrice();
                if (totalSum < bestResult) {
                    res.clear();
                    res.add(clone);
                }
            } catch (CloneNotSupportedException ignore) {
                //throw when object not supported Cloneable
            }
        }
    }

    private List<List<Purchase>> executeTasks(int numberOfGallons, Set<Purchase> purchaseSet) {
        List<List<Purchase>> result = new ArrayList<>();
        //price priority
        result.add(executeTask(purchaseSet.stream()
                .sorted(Comparator.comparing(Purchase::getPriceOfGallon))
                .collect(Collectors.toList()), numberOfGallons));

        //price + max(minSize) priority
        result.add(executeTask(purchaseSet.stream().sorted((p1, p2) -> {
            if (Objects.equals(p1.getPriceOfGallon(), p2.getPriceOfGallon())) {
                return p2.getMinSize().compareTo(p1.getMinSize());
            } else {
                return p1.getPriceOfGallon().compareTo(p2.getPriceOfGallon());
            }
        }).collect(Collectors.toList()), numberOfGallons));

        //max(minSize) + price priority
        result.add(executeTask(purchaseSet.stream().sorted((p1, p2) -> {
            if (Objects.equals(p1.getMinSize(), p2.getMinSize())) {
                return p1.getPriceOfGallon().compareTo(p2.getPriceOfGallon());
            } else {
                return p2.getMinSize().compareTo(p1.getMinSize());
            }
        }).collect(Collectors.toList()), numberOfGallons));

        //min(minSize) priority
        result.add(executeTask(purchaseSet.stream()
                .sorted((p1, p2) -> {
                    if (Objects.equals(p1.getPriceOfGallon(), p2.getPriceOfGallon())) {
                        return p1.getMinSize().compareTo(p2.getMinSize());
                    } else {
                        return p1.getPriceOfGallon().compareTo(p2.getPriceOfGallon());
                    }
                })
                .collect(Collectors.toList()), numberOfGallons));

        //price + min(minSize) priority
        result.add(executeTask(purchaseSet.stream().sorted((p1, p2) -> {
            if (Objects.equals(p1.getPriceOfGallon(), p2.getPriceOfGallon())) {
                return p1.getMinSize().compareTo(p2.getMinSize());
            } else {
                return p1.getPriceOfGallon().compareTo(p2.getPriceOfGallon());
            }
        }).collect(Collectors.toList()), numberOfGallons));


        //min(minSize) + price priority
        result.add(executeTask(purchaseSet.stream().sorted((p1, p2) -> {
            if (Objects.equals(p1.getMinSize(), p2.getMinSize())) {
                return p1.getPriceOfGallon().compareTo(p2.getPriceOfGallon());
            } else {
                return p1.getMinSize().compareTo(p2.getMinSize());
            }
        }).collect(Collectors.toList()), numberOfGallons));

        return result;
    }

    private List<Purchase> executeTask(List<Purchase> sorted, int numberOfGallons) {
        FutureTask<List<Purchase>> pricePriorityTask = new FutureTask<>(new CalculateByGreedyCallableTask(
                sorted, numberOfGallons, this));
        new Thread(pricePriorityTask).start();
        try {
            return pricePriorityTask.get();
        } catch (InterruptedException | ExecutionException e) {
            //it's impossible
            return new ArrayList<>();
        }
    }

    private List<Purchase> fillByGreedyAlgorithm(int numberOfGallons, List<Purchase> sorted) {
        List<Purchase> res = new ArrayList<>();

        for (Purchase purchase : sorted) {
            Integer currentNumberOfGallons = purchase.getNumberOfGallons();
            if (currentNumberOfGallons < numberOfGallons) {
                res.add(purchase);
                numberOfGallons = numberOfGallons - currentNumberOfGallons;
                if (numberOfGallons == 0) {
                    break;
                }
            } else {
                if (purchase.getMinSize() > numberOfGallons || purchase.getStepSize() > numberOfGallons) {
                    continue;
                }

                try {
                    Purchase clone = (Purchase) purchase.clone();
                    currentNumberOfGallons = currentNumberOfGallons - purchase.getMinSize();
                    numberOfGallons = numberOfGallons - purchase.getMinSize();
                    int diff = purchase.getMinSize();

                    while (currentNumberOfGallons > 0) {
                        if (currentNumberOfGallons - purchase.getStepSize() >= 0 && numberOfGallons - purchase.getStepSize() >= 0) {
                            numberOfGallons -= purchase.getStepSize();
                            currentNumberOfGallons -= purchase.getStepSize();
                            diff += purchase.getStepSize();
                        } else {
                            break;
                        }
                    }

                    clone.setNumberOfGallons(diff);
                    res.add(clone);
                    if (numberOfGallons == 0) {
                        break;
                    }
                } catch (CloneNotSupportedException ignore) {
                    //throw when object not supported Cloneable
                }
            }
        }
        return res;
    }

    private static class CalculateByGreedyCallableTask implements Callable<List<Purchase>> {
        private List<Purchase> sorted;
        private int numberOfGallons;
        private PurchaseCalculator calculator;

        CalculateByGreedyCallableTask(List<Purchase> sorted, int numberOfGallons, PurchaseCalculator calculator) {
            this.sorted = sorted;
            this.numberOfGallons = numberOfGallons;
            this.calculator = calculator;
        }


        @Override
        public List<Purchase> call() throws Exception {
            return calculator.fillByGreedyAlgorithm(numberOfGallons, sorted);
        }
    }
}
