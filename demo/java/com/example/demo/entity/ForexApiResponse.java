package com.example.demo.entity;

import java.time.LocalDate;
import java.util.Map;

public class ForexApiResponse {
    private double amount;
    private String base;
    private LocalDate date;
    private Map<String, Double> rates;



    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

	@Override
	public String toString() {
		return "ForexApiResponse [amount=" + amount + ", base=" + base + ", date=" + date + ", rates=" + rates + "]";
	}
}