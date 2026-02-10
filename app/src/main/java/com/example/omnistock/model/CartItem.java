package com.example.omnistock.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private int cartId;
    private int productId;
    private String name;
    private double price;
    private String imageBase64;
    private int qty;
    private boolean isChecked = false;

    public CartItem(int cartId, int productId, String name, double price, String imageBase64, int qty) {
        this.cartId = cartId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageBase64 = imageBase64;
        this.qty = qty;
    }

    protected CartItem(Parcel in) {
        cartId = in.readInt();
        productId = in.readInt();
        name = in.readString();
        price = in.readDouble();
        imageBase64 = in.readString();
        qty = in.readInt();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public int getCartId() { return cartId; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageBase64() { return imageBase64; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cartId);
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(imageBase64);
        dest.writeInt(qty);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}