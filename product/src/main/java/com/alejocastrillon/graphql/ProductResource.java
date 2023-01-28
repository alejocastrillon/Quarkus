package com.alejocastrillon.graphql;

import com.alejocastrillon.entities.Product;
import com.alejocastrillon.repositories.ProductRepository;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import javax.inject.Inject;
import java.util.List;

@GraphQLApi
public class ProductResource {

    @Inject
    private ProductRepository productRepository;

    @Query("allProducts")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Query("product")
    public Product getProduct(@Name("productId") long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }

}
