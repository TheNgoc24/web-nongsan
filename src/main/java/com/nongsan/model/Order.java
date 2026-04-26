package com.nongsan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double total;

    private LocalDateTime createdAt;

    private String name;
    private String phone;
    private String address;

    public Order() {}

    public Order(double total) {
        this.total = total;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public double getTotal() { return total; }

    public void setTotal(double total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}