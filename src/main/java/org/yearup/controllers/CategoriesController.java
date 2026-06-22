package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.service.CategoryService;
import org.yearup.service.ProductService;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests

@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController
{
    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    CategoriesController(CategoryService categoryService, ProductService productService){
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Category>> getAll()
    {
        List<Category> categories = categoryService.getAllCategories();
           return ResponseEntity.ok(categories);
    }

    // add the appropriate annotation for a get action
    // https://localhost:8080/categories/1
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Category> getByCategoryId(@PathVariable Integer id){
        Category category = categoryService.getByCategoryId(id);

        if (categoryService.getByCategoryId(id) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(category);
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Integer categoryId)
    {
        List<Product> product = productService.getProductsByCategoryId(categoryId);
        if (categoryId == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  ResponseEntity.ok(product);
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> addCategory(@RequestBody Category category)
    {
        Category saved = categoryService.createCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        Category updated = categoryService.update(id, category);

        return ResponseEntity.ok(updated);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id)
    {
        if(categoryService.getByCategoryId(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

       categoryService.delete(id);
       return ResponseEntity.noContent().build();
    }
}
