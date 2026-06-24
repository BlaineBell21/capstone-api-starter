package org.yearup.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.yearup.models.Product;
import org.yearup.service.ProductService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void allProducts_shouldReturnListOfAllProducts() throws Exception{
        Product product1 = new Product(
                1,
                "name 1",
                10.99,
                1,
                "a test product 1",
                "a test sub category 1",
                20,
                true,
                "url 1" );

        Product product2 = new Product(
                2,
                "name 2",
                9.99,
                1,
                "a test product 2",
                "a test sub category 2",
                10,
                true,
                "url 2" );
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.search(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(products);
        
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("a test product 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].imageUrl").value("url 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].featured").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }


    @Test
    @PreAuthorize("permitAll()")
    void getById_shouldReturnProductById() throws Exception {
        Product product1 = new Product(
                1,
                "name 1",
                10.99,
                1,
                "a test product 1",
                "a test sub category 1",
                20,
                true,
                "url 1" );

        when(productService.getById(1)).thenReturn(product1);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/products/1", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("a test product 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subCategory").value("a test sub category 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.featured").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value("url 1"));
    }

    @Test
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void addProduct_shouldCreateProductAndPersist() throws Exception {
        Product savedProduct = new Product(
                1,
                "name 1",
                10.99,
                1,
                "a test product 1",
                "a test sub category 1",
                20,
                true,
                "url 1");

        when(productService.create(any(Product.class)))
                .thenReturn(savedProduct);

        mockMvc.perform(post("http://localhost:8080/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "productId": 1,
                            "name": "name 1",
                            "price": 10.99,
                            "categoryId": 1,
                            "description": "a test product 1",
                            "subCategory": "a test sub category 1",
                            "stock": 20,
                            "isFeatured": true,
                            "imageUrl": "url 1"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isCreated())  // Should be 201 but our code returns 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("a test product 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subCategory").value("a test sub category 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.featured").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value("url 1"));
    }

    @Test
    void updateProduct_shouldUpdateProduct() throws Exception {
        Product savedProduct = new Product(
                1,
                "name changed",
                10.99,
                1,
                "a test product 1",
                "a test sub category 1",
                40,
                true,
                "url 1");

        when(productService.getById(1)).thenReturn(savedProduct);

        when(productService.update(eq(1), any(Product.class)))
                .thenReturn(savedProduct);

        mockMvc.perform(put("http://localhost:8080/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "productId": 1,
                            "name": "name changed",
                            "price": 10.99,
                            "categoryId": 1,
                            "description": "a test product 1",
                            "subCategory": "a test sub category 1",
                            "stock": 40,
                            "isFeatured": true,
                            "imageUrl": "url 1"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isOk())  // Should be 201 but our code returns 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name changed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("a test product 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subCategory").value("a test sub category 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(40))
                .andExpect(MockMvcResultMatchers.jsonPath("$.featured").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value("url 1"));
    }

    @Test
    void deleteProduct_shouldDeleteProduct() throws Exception {
        int id = 1;

        when(productService.getById(id)).thenReturn(new Product());
        doNothing().when(productService).delete(id);

        mockMvc.perform(delete("http://localhost:8080/products/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(id);

    }
}