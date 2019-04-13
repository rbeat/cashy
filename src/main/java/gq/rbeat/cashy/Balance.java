package gq.rbeat.cashy;

public class Balance {
    private double available;
    private double personalBalance;
    private double creditBalance;
    private double debt;

    public Balance() {
        this.personalBalance = 0;
        this.creditBalance = 0;

        this.available = personalBalance + creditBalance - debt;
    }

    public Balance(double personalBalance, double creditBalance) {
        this.creditBalance = creditBalance;
        this.personalBalance = personalBalance;
        this.available = personalBalance + creditBalance - debt;
    }


    public void setDebt(Double debt) {
        this.debt = debt;
    }

    public double getPersonalBalance() {
        return personalBalance;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public double getAvailable() {
        return available;
    }

    public void setPersonalBalance(double balance) {
        this.personalBalance = balance;
    }

    public void setCreditBalance(double balance) {
        this.creditBalance = balance;
    }

    public void recalculateAvailable() {

        this.available = personalBalance + creditBalance - debt;
    }

    public void spend(double toSpend) {
        if (personalBalance < toSpend) {
            toSpend -= personalBalance;
            personalBalance = 0;
            creditBalance -= toSpend;
        } else {
            personalBalance -= toSpend;
        }
        recalculateAvailable();
    }
}
