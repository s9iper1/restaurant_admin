package com.byteshaft.restaurantadmin.gettersetter;


import android.widget.ImageView;

public class AddTable {

    private String tableNumber;
    private String tableStatus;
    private ImageView tableImage;

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public ImageView getTableImage() {
        return tableImage;
    }

    public void setTableImage(ImageView tableImage) {
        this.tableImage = tableImage;
    }

}
