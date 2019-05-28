package com.example.jstore_android_muhammaddjatipradana;

public class Location {
    private String province;
    private String description;
    private String city;

    public Location(String province, String city, String description)
    {
        this.province=province;
        this.description=description;
        this.city=city;
    }

    public String getProvince(){
        return province;
    }

    public String getCity(){
        return city;
    }

    public String getDescription(){
        return description;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString()
    {
        return "= Location ===============================" +
                "\nProvince      : " + province +
                "\nCity          : " + city +
                "\nDescription   : " + description +
                "\n==========================================";
    }
}
