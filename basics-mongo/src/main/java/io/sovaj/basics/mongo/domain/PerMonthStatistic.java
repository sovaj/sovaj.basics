package io.sovaj.basics.mongo.domain;

/**
 *
 * @author Mickael Dubois
 */
public class PerMonthStatistic {

    private String month;
    private String year;
    private int total;

    public String getMonth() {
        if (month.length() == 1) {
            month = "0" + month;
        }
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
