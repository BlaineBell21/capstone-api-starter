package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.*;
import org.yearup.repository.ShoppingCartRepository;
import org.yearup.utils.ValidationCheck;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

        for (CartItem cartItem : cartItems){
           Product product = productService.getById(cartItem.getProductId());

           ShoppingCartItem item = new ShoppingCartItem();

           item.setProduct(product);
           item.setQuantity(cartItem.getQuantity());

           cart.add(item);
        }
        return cart;
    }

    public ShoppingCart addItem(int productId, int userId){

        CartItem existingCartItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        Product product = productService.getById(productId);

        ValidationCheck.productValidation(product); // Checks if product exists

       if (existingCartItem != null){
           int updatedQuantity = existingCartItem.getQuantity() + 1;
           existingCartItem.setQuantity(updatedQuantity);
           shoppingCartRepository.save(existingCartItem);
       }else {
           CartItem newCartItem = new CartItem();
           newCartItem.setProductId(productId);
           newCartItem.setUserId(userId);
           newCartItem.setQuantity(1);
           shoppingCartRepository.save(newCartItem);
       }
        return getByUserId(userId);
    }

    public ShoppingCart updateItem(int userId, int productId, ShoppingCartItem item){
        System.out.println("Incoming quantity: " + item.getQuantity());
        Product product = productService.getById(productId);
        System.out.println("Request item found : " + product.getName());
        ValidationCheck.productValidation(product); // Checks if product exists

       CartItem existingCartItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);


       existingCartItem.setQuantity(item.getQuantity());

       ValidationCheck.quantityCheck(existingCartItem, product);
       // Checks if inputted quantity is higher than 0 but less than maximum stock
        shoppingCartRepository.save(existingCartItem);
       return getByUserId(userId);
    }

    @Transactional
    public ShoppingCart clearCart(int userId){
        shoppingCartRepository.deleteByUserId(userId);
        return getByUserId(userId);
    }
}
