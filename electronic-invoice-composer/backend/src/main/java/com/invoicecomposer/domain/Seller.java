package com.invoicecomposer.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the seller (issuer) of an electronic invoice.
 */
public class Seller {

    @NotBlank(message = "Seller name is required")
    private String name;

    @NotBlank(message = "Seller NIT is required")
    private String nit;

    private String address;
    private String city;
    private String phone;

    public Seller() {
    }

    public Seller(String name, String nit, String address, String city, String phone) {
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.city = city;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
