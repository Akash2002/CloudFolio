package com.example.akash.fbla_library;

/**
 * Created by akash on 12/30/2017.
 */

public class HoldDataManager {
    private String checkoutDateLimit;
    private String dateBookHeld;
    private String daysToWait;
    private String stockArrivalDate;



    public HoldDataManager(String checkoutDateLimit, String dateBookHeld, String daysToWait, String stockArrivalDate) {
        this.checkoutDateLimit = checkoutDateLimit;
        this.dateBookHeld = dateBookHeld;
        this.daysToWait = daysToWait;
        this.stockArrivalDate = stockArrivalDate;
    }

    public String getCheckoutDateLimit() {
        return checkoutDateLimit;
    }

    public void setCheckoutDateLimit(String checkoutDateLimit) {
        this.checkoutDateLimit = checkoutDateLimit;
    }

    public String getDateBookHeld() {
        return dateBookHeld;
    }

    public void setDateBookHeld(String dateBookHeld) {
        this.dateBookHeld = dateBookHeld;
    }

    public String getDaysToWait() {
        return daysToWait;
    }

    public void setDaysToWait(String daysToWait) {
        this.daysToWait = daysToWait;
    }

    public String getStockArrivalDate() {
        return stockArrivalDate;
    }

    public void setStockArrivalDate(String stockArrivalDate) {
        this.stockArrivalDate = stockArrivalDate;
    }
}
