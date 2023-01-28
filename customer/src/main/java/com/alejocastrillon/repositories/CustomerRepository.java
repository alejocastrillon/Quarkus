package com.alejocastrillon.repositories;

import com.alejocastrillon.entities.Customer;
import com.alejocastrillon.entities.Product;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.core.Field;
import io.smallrye.graphql.client.core.Operation;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    @GraphQLClient("product-dynamic-client")
    DynamicGraphQLClient dynamicGraphQLClient;

    /*@Inject
    CriteriaBuilderFactory criteriaBuilderFactory;*/

    /*@Inject
    EntityViewManager entityViewManager;*/

    @Transactional
    public void createCustomer(Customer customer) {
        entityManager.persist(customer);
    }

    /*@Transactional
    public List<Customer> getCustomers() {
        //return entityManager.createQuery("SELECT c FROM Customer c").getResultList();
        CriteriaBuilder<Customer> criteriaBuilder = criteriaBuilderFactory.create(entityManager, Customer.class);
        List<CustomerView> customerViews = entityViewManager.applySetting(EntityViewSetting.create(CustomerView.class),
                criteriaBuilder).getResultList();
        return customerViews.stream().map(cv -> {
            Customer c = new Customer();
            c.setId(cv.getId());
            c.setCode(cv.getCode());
            c.setAccountNumber(cv.getAccountNumber());
            c.setName(cv.getName());
            return c;
        }).collect(Collectors.toList());

    }*/

    @Transactional
    public void deleteCustomer(Customer customer) {
        entityManager.remove(customer);
    }

    @Transactional
    public Customer getCustomer(long id) {
        return entityManager.find(Customer.class, id);
    }

    public List<Product> getProductsGraphQl() throws Exception {
        Document document = Document.document(
                Operation.operation(
                        Field.field("allProducts", Field.field("id"), Field.field("name"),
                                Field.field("description")))
        );
        Response response = dynamicGraphQLClient.executeSync(document);
        return response.getList(Product.class,"allProducts");
    }

    public Product getProductGraphQl(long id) throws Exception {
        Document document = Document.document(
                Operation.operation(
                        Field.field(String.format("product(productId: %d", id), Field.field("id"),
                                Field.field("name"), Field.field("description"))
                )
        );
        Response response = dynamicGraphQLClient.executeSync(document);
        return response.getObject(Product.class, "product");
    }
}
