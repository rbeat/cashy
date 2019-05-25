package gq.rbeat.cashy;

import java.util.ArrayList;
import java.util.List;

public class Payment {
    private List<String> name;
    private List<Double> sum;

    public Payment() {
        name = new ArrayList<>();
        sum = new ArrayList<>();
    }

    public void makePayment(String name, Double sum, Balance balance) {
        this.name.add(name);
        this.sum.add(sum);
        balance.spend(sum);
    }

    public void removeLast() {
        this.name.remove(this.name.size() - 1);
        this.sum.remove(this.name.size() - 1);
    }

    public List<String> getName() {
        return this.name;
    }

    public List<Double> getSum() {
        return this.sum;
    }

    public void removePayment(int i) {
        this.name.remove(i);
        this.sum.remove(i);
    }
}

