package com.alejocastrillon;

import com.alejocastrillon.entities.Product;
import com.alejocastrillon.repositories.ProductRepository;
import lombok.AllArgsConstructor;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/product")
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GET
    public List<Product> hello() {
        return productRepository.getProducts();
    }

    @POST
    public Response add(Product product) {
        productRepository.createdProduct(product);
        return Response.ok().build();
    }

    @DELETE
    public Response delete(Product product) {
        productRepository.deleteProduct(product);
        return Response.ok().build();
    }
}