package com.goldenKids.dotami;

public class ListItem {
    public String location,date,latlong,url;
    public ListItem(String location,String date,String latlong,String url){
        this.location =location;
        this.date = date;
        this.latlong =latlong;
        this.url =url;
    }

    public String getDate() {
        return date;
    }

    public String getLatlong() {
        return latlong;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }
}
