package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService
{
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    public List<Product> search(Integer categoryId, Double minPrice, Double maxPrice, String subCategory)
    {
        List<Product> products = categoryId != null
                ? productRepository.findByCategoryId(categoryId)
                : productRepository.findAll();

        return products.stream()
                       .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                       .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                       .filter(p -> subCategory == null || subCategory.equalsIgnoreCase(p.getSubCategory()))
                       .filter(Product::isFeatured)
                       .toList();
    }

    public Product getById(Integer productId)
    {
        return productRepository.findById(productId).orElse(null);
    }

    public List<Product> getProductsByCategoryId(Integer categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    public Product create(Product product)
    {
        product.setProductId(0);
        return productRepository.save(product);
    }

    public Product update(int productId, Product product)
    {
        Product existing = productRepository.findById(productId).orElseThrow();
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setCategoryId(product.getCategoryId());
        existing.setDescription(product.getDescription());
        existing.setSubCategory(product.getSubCategory());
        existing.setFeatured(product.isFeatured());
        existing.setImageUrl(product.getImageUrl());
        return productRepository.save(existing);
    }

    public void delete(int productId)
    {
        productRepository.deleteById(productId);
    }
}
