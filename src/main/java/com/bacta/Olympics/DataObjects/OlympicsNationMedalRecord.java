package com.bacta.Olympics.DataObjects;


public class OlympicsNationMedalRecord {
    private int gold;
    private int silver;
    private int bronze;
    private int toatl;
    private String countryName;
    private String flagUrl;

    public OlympicsNationMedalRecord(String countryName, String flagUrl, int gold, int silver, int bronze, int total) {
        this.countryName = countryName;
        this.flagUrl = flagUrl;
        this.gold = gold;
        this.silver = silver;
        this.bronze = bronze;
        this.toatl = total;
    }

    public int getPoints() {
        return gold * 3 + silver * 2 + bronze;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }

    public int getTotal() {
        return toatl;
    }

    public void setToatl(int toatl) {
        this.toatl = toatl;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    @Override
    public String toString() {
        return "Olympic Medal Record" +
                "\nFlag: " + flagUrl +
                "\nCountry: " + countryName +
                "\nGold: " + gold +
                "\nSilver: " + silver +
                "\nBronze: " + bronze +
                "\nTotal: " + toatl;
    }
}
