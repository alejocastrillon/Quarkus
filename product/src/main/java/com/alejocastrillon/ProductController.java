package com.alejocastrillon;

import com.alejocastrillon.entities.Product;
import com.alejocastrillon.repositories.ProductRepository;
import lombok.AllArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/product")
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GET
    public List<Product> hello() {
        return productRepository.findAll();
    }

    @POST
    public Response add(Product product) {
        productRepository.save(product);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Product getProductById(@PathParam("id") long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        productRepository.delete(getProductById(id));
        return Response.ok().build();
    }
}