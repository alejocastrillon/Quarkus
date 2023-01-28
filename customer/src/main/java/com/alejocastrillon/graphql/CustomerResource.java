package com.alejocastrillon.graphql;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.repositories.CustomerPanacheRepository;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;

import javax.inject.Inject;
import java.util.List;

@GraphQLApi
public class CustomerResource {

    @Inject
    private CustomerPanacheRepository panacheRepository;

    @Query("allCustomer")
    @Description("Obtiene los clientes registrados en base de datos")
    public Uni<List<Customer>> getAllCustomers() {
        return panacheRepository.listAll();
    }

    @Query("customer")
    public Uni<Customer> getCustomer(@Name("id") long id) {
        return panacheRepository.findById(id);
    }

    @Mutation
    public Uni<Customer> addCustomer(Customer customer) {
        if (customer.getProducts() != null && !customer.getProducts().isEmpty()) {
            customer.getProducts().forEach(p -> p.setCustomer(customer));
        }
        return panacheRepository.persist(customer);
    }

    @Mutation
    public Uni<Boolean> deleteCustomer(long id) {
        return panacheRepository.deleteById(id);
    }

}
