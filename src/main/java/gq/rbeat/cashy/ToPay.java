package gq.rbeat.cashy;

import java.util.ArrayList;
import java.util.List;

public class ToPay {
    private List<String> name;
    private List<Double> sum;

    public ToPay() {
        name = new ArrayList<>();
        sum = new ArrayList<>();
    }

    public void addToPay(String name, Double sum) {
        this.name.add(name);
        this.sum.add(sum);

    }

    public double calculateSum() {
        double sum = 0;
        for (int i = 0; i < this.sum.size(); i++) {
            sum += this.sum.get(i);
        }
        return sum;
    }

    public void removeToPay(String name) {
        for (int i = 0; i < sum.size(); i++) {
            if (this.name.get(i).equals(name)) {
                this.name.remove(i);
                this.sum.remove(i);
            }
        }
    }
}
