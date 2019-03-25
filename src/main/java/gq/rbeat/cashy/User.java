package gq.rbeat.cashy;

import java.lang.reflect.ParameterizedType;

public class User {
    private String name;
    private String email;
    private Balance balance;
    private ToPay toPay;
    private Payment payment;

    public User() {
        this.name = "John Appleseed";
        this.email = "test@john.as";
        this.balance = new Balance();
        this.toPay = new ToPay();
        this.payment = new Payment();
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.balance = new Balance();
        this.toPay = new ToPay();
        this.payment = new Payment();

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addToPay(String name, Double sum) {
        toPay.addToPay(name, sum);
        balance.setToPay(toPay.calculateSum());
    }

    public void removeToPay(String name) {
        toPay.removeToPay(name);
    }

    public void makePayment(String name, Double sum, Balance balance) {
        payment.makePayment(name, sum, balance);
    }

    public void removeLast() {
        payment.removeLast();
    }

    public double getPersonalBalance() {
        return this.balance.getPersonalBalance();
    }

    public double getCreditBalance() {
        return this.balance.getCreditBalance();
    }

    public double getAvailable() {
        return this.balance.getAvailable();
    }

    public void setPersonalBalance(double balance) {
        this.balance.setPersonalBalance(balance);
        this.recalculateAvailable();
    }

    public void setCreditBalance(double balance) {
        this.balance.setCreditBalance(balance);
        this.recalculateAvailable();
    }

    public void recalculateAvailable() {
        this.balance.recalculateAvailable();
    }

    public void spend(double toSpend) {
        this.balance.spend(toSpend);
    }

}
