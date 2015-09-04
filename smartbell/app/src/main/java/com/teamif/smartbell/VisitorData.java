package com.teamif.smartbell;

/**
 * Created by user on 2015-02-10.
 */
public class VisitorData
{

    private String purpose;
    private String name;
    private String thumbnailURL;
    private String time;
    private String visitorID;

    public VisitorData()
    {

    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVisitorID() {
        return visitorID;
    }

    public void setVisitorID(String visitorID) {
        this.visitorID = visitorID;
    }
}
