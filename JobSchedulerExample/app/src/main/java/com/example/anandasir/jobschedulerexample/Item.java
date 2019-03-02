package com.example.anandasir.jobschedulerexample;

public class Item {

    String txtURL;
    String txtStatus;

    Item(String txtURL, String txtStatus){
        this.txtURL = txtURL;
        this.txtStatus = txtStatus;
    }

    public String getTxtURL() {
        return txtURL;
    }

    public void setTxtURL(String txtURL) {
        this.txtURL = txtURL;
    }

    public String getTxtStatus() {
        return txtStatus;
    }

    public void setTxtStatus(String txtStatus) {
        this.txtStatus = txtStatus;
    }
}
