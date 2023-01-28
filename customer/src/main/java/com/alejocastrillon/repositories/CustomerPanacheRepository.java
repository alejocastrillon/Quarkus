package com.alejocastrillon.repositories;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.entities.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class CustomerPanacheRepository implements PanacheRepository<Customer> {

    @Inject
    private Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8081)
                .setSsl(false).setTrustAll(true));
    }

    public Uni<Customer> add(Customer customer) {
        return persist(customer);
    }

    public Uni<Customer> getByIdProduct(@PathParam("id") long id) {
        return Uni.combine().all().unis(getCustomerReactive(id), getProductsByCustomer())
                .combinedWith((customer, products) -> {
                    customer.getProducts().forEach(p -> {
                        /*Product product = products.stream().filter(pro -> pro.getId() == p.getProduct()).findFirst()
                                .get();
                        p.setName(product.getName());
                        p.setDescription(product.getDescription());*/
                    });
                    return customer;
                });
    }

    public Uni<Customer> getCustomerReactive(long id) {
        return findById(id);
    }

    private Uni<List<Product>> getProductsByCustomer() {
        return webClient.get(8081, "localhost", "/product").send().onFailure()
                .invoke(err -> log.error("Error: {}", err)).onItem().transform(res -> {
                    List<Product> products = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();
                    objects.forEach(p -> {
                        log.info("Analizando objecto {}", p);
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            products.add(mapper.readValue(p.toString(), Product.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return products;
                });
    }

    public Uni<Boolean> deleteCustomer(long id) {
        return deleteById(id);
    }

    public Uni<List<Customer>> listCustomers() {
        return listAll(Sort.by("name"));
    }
}
