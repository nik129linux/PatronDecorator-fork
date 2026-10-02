package com.invoicecomposer.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the customer (buyer) of an electronic invoice.
 */
public class Customer {

    @NotBlank(message = "Customer name is required")
    private String name;

    @NotBlank(message = "Customer NIT is required")
    private String nit;

    @Email(message = "Customer email must be valid")
    private String email;

    private String address;
    private String city;
    private String phone;

    public Customer() {
    }

    public Customer(String name, String nit, String email, String address, String city, String phone) {
        this.name = name;
        this.nit = nit;
        this.email = email;
        this.address = address;
        this.city = city;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /** Prototype support: independent copy of this customer. */
    public Customer copy() {
        return new Customer(name, nit, email, address, city, phone);
    }
}
