package com.alejocastrillon.repositories;

import com.alejocastrillon.entities.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ProductRepository {

    @Inject
    EntityManager em;

    @Transactional
    public void createdProduct(Product product) {
        em.persist(product);
    }

    @Transactional
    public List<Product> getProducts() {
        return em.createQuery("SELECT p FROM Product p").getResultList();
    }

    @Transactional
    public void deleteProduct(Product product) {
        em.remove(product);
    }

    public Product getProductById(long id) {
        return em.find(Product.class, id);
    }

}
