package gq.rbeat.cashy;

public class Balance {
    private double available;
    private double personalBalance;
    private double creditBalance;
    private double toPay;

    public Balance() {
        this.personalBalance = 0;
        this.creditBalance = 0;
        this.available = personalBalance + creditBalance - toPay;
    }

    public Balance(double personalBalance, double creditBalance) {
        this.creditBalance = creditBalance;
        this.personalBalance = personalBalance;
        this.available = personalBalance + creditBalance;
    }

    public void setToPay(double toPay) {
        this.toPay = toPay;
    }

    public double getPersonalBalance() {
        return personalBalance;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public double getavailable() {
        return available;
    }

    public void setPersonalBalance(double balance) {
        this.personalBalance = balance;
    }

    public void recalculateAvailable() {
        this.available = personalBalance + creditBalance - toPay;
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
