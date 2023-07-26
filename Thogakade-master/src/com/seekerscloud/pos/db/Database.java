package com.seekerscloud.pos.db;

import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;

import java.util.ArrayList;

public class Database {
    public static ArrayList<Customer> customerTable = new ArrayList<Customer>();
    public static ArrayList<Item> itemTable = new ArrayList<Item>();

    static {
        customerTable.add(new Customer("C001","Bandara","Colombo",25000));
        customerTable.add(new Customer("C002","Supun","Galle",35000));
        customerTable.add(new Customer("C003","Banda","Colombo",45000));
        customerTable.add(new Customer("C004","Dara","Panadura",50000));
        customerTable.add(new Customer("C005","Andar","Matara",65000));

        itemTable.add(new Item("I-001","D1",25,20));
        itemTable.add(new Item("I-002","D2",35,30));
        itemTable.add(new Item("I-003","D3",45,40));
        itemTable.add(new Item("I-004","D4",50,50));
        itemTable.add(new Item("I-005","D5",20,25));
    }

}
