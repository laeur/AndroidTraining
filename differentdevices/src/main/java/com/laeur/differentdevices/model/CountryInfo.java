package com.laeur.differentdevices.model;

/**
 * Created by svareug on 17/04/2017.
 */

public class CountryInfo {

    private String countryName;
    private String capitalName;
    private String additionalInfo;

    public CountryInfo(String unknown) {
        this.countryName = unknown;
        this.capitalName = unknown;
        this.additionalInfo = unknown;
    }

    public CountryInfo(String countryName, String capitalName, String flagUri) {
        this.countryName = countryName;
        this.capitalName = capitalName;
        this.additionalInfo = flagUri;
    }
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCapitalName() {
        return capitalName;
    }

    public void setCapitalName(String capitalName) {
        this.capitalName = capitalName;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
