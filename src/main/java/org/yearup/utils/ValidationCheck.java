package org.yearup.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.CartItem;
import org.yearup.models.Product;

public class ValidationCheck {
    public static void productValidation(Product product){

        if(product == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found.\n" +
                            "This product either isn't available or the Product Id was entered in incorrectly.");
        }
    }

    public static void quantityCheck(CartItem cartItem, Product product){
        if (cartItem.getQuantity() < 1 || product.getStock() < cartItem.getQuantity()){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Illegal item quantity.\n" +
                            "Inputted item quantity must be greater than 0 and less than maximum stock amount.");
        }
    }
}
