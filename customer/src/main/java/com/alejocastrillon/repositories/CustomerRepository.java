package com.alejocastrillon.repositories;

import com.alejocastrillon.entities.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerRepository {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void createCustomer(Customer customer) {
        entityManager.persist(customer);
    }

    @Transactional
    public List<Customer> getCustomers() {
        return entityManager.createQuery("SELECT c FROM Customer c").getResultList();
    }

    @Transactional
    public void deleteCustomer(Customer customer) {
        entityManager.remove(customer);
    }

    @Transactional
    public Customer getCustomer(long id) {
        return entityManager.find(Customer.class, id);
    }

}
