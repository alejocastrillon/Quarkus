package com.alejocastrillon;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.entities.Product;
import com.alejocastrillon.repositories.CustomerPanacheRepository;
import com.alejocastrillon.repositories.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/customer")
@Slf4j
public class CustomerController {

    @Inject
    private CustomerRepository repository;

    @Inject
    private CustomerPanacheRepository panacheRepository;

    @Inject
    private Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8081)
                .setSsl(false).setTrustAll(true));
    }

    @POST
    public Uni<Customer> add(Customer customer) {
        customer.getProducts().forEach(p -> p.setCustomer(customer));
        return panacheRepository.persist(customer);
    }

    @GET
    public Uni<List<Customer>> getCustomers() {
        return panacheRepository.listCustomers();
    }

    @GET
    @Path("/{id}")
    public Uni<Customer> getByIdProduct(@PathParam("id") long id) {
        return panacheRepository.getByIdProduct(id);
    }

    @DELETE
    public Uni<Response> deleteCustomer(@QueryParam("id") int id) {
        return panacheRepository.deleteCustomer(id).map(isDeleted -> isDeleted ? Response.status(Response.Status
                .NO_CONTENT).build() : Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/products-graphql")
    @Blocking
    public List<Product> getProductsGraphQl() throws Exception {
        return repository.getProductsGraphQl();
    }

    @GET
    @Path("/product-graphql/{id}")
    @Blocking
    public Product getProductGraphQl(@PathParam("id") long id) throws Exception {
        return repository.getProductGraphQl(id);
    }

}