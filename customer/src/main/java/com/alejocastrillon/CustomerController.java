package com.alejocastrillon;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.entities.Product;
import com.alejocastrillon.repositories.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
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
    private Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8081)
                .setSsl(false).setTrustAll(true));
    }

    @POST
    public Response add(Customer customer) {
        customer.getProducts().forEach(p -> p.setCustomer(customer));
        repository.createCustomer(customer);
        return Response.ok().build();
    }

    @GET
    public Response getCustomers() {
        return Response.ok(repository.getCustomers()).build();
    }

    @GET
    @Path("/{id}")
    @Blocking
    public Uni<Customer> getByIdProduct(@PathParam("id") long id) {
        return Uni.combine().all().unis(getCustomerReactive(id), getProductsByCustomer())
                .combinedWith((customer, products) -> {
                    customer.getProducts().forEach(p -> {
                        Product product = products.stream().filter(pro -> pro.getId() == p.getProduct()).findFirst()
                                .get();
                        p.setName(product.getName());
                        p.setDescription(product.getDescription());
                    });
                    return customer;
                });
    }

    @DELETE
    public Response deleteCustomer(@QueryParam("id") int id) {
        Customer customer = repository.getCustomer(id);
        if (customer != null) {
            repository.deleteCustomer(customer);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Uni<Customer> getCustomerReactive(long id) {
        Customer customer = repository.getCustomer(id);
        return Uni.createFrom().item(customer);
    }

    private Uni<List<Product>> getProductsByCustomer() {
        return webClient.get(8081, "localhost", "/product").send().onFailure()
                .invoke(err -> log.error("Error: {}", err)).onItem().transform(res -> {
                    List<Product> products = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();
                    objects.forEach(p -> {
                        log.info("Analizando objecto {}", p);
                        ObjectMapper mapper = new ObjectMapper();
                        Product product = null;
                        try {
                            product = mapper.readValue(p.toString(), Product.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        products.add(product);
                    });
                    return products;
                });
    }
}