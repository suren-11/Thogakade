package com.seekerscloud.pos.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "Item")
public class Item implements SuperEntity{
    @Id
    @Column(name = "item_code")
    private String code;
    @Column(name = "description",
            nullable = false)
    private String description;
    @Column(name = "unit_Price",
            nullable = false)
    private double unitPrice;
    @Column(name = "qty_On_Hand",
            nullable = false)
    private int qtyOnHand;

    //------------------------
    @OneToMany(mappedBy = "item", cascade = {
            CascadeType.ALL
    })
    private List<OrderDetails> details = new ArrayList<>();

    //------------------------


    public Item() {
    }

    public Item(String code, String description, double unitPrice, int qtyOnHand) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }
}
