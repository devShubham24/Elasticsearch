package com.sb.main.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products") 
public class Product {

    @Id
    private String id;
    private String name;
    private String colour;
    private double price;

    // Constructors
    public Product() {}

    public Product(String id, String name, String colour, double price) {
        this.id = id;
        this.name = name;
        this.colour = colour;
        this.price = price;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // toString() method for debugging
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", colour='" + colour + '\'' +
                ", price=" + price +
                '}';
    }
}
