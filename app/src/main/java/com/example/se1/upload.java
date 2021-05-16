package com.example.se1;

public class upload {
    private String itemName;
    private String imageUri;
    private String itemQuantity;
    private String description;

    public upload(){

    }

    public upload(String imageUri, String itemName, String itemQuantity, String description) {
        this.itemName = itemName;
        this.imageUri = imageUri;
        this.itemQuantity = itemQuantity;
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
