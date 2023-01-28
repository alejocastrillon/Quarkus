package com.alejocastrillon.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String accountNumber;

    private String name;

    private String lastname;

    private String phone;

    private String address;

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

}
