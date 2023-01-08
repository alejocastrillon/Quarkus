package com.alejocastrillon;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.repositories.CustomerRepository;
import lombok.AllArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerRepository repository;

    @POST
    public Response add(Customer customer) {
        repository.createCustomer(customer);
        return Response.ok().build();
    }

    @GET
    public Response getCustomers() {
        return Response.ok(repository.getCustomers()).build();
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
}