package org.yearup.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.*;
import org.yearup.repository.OrderRepository;
import org.yearup.utils.DateUtils;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;
    private final ReceiptService receiptService;

    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, ProfileService profileService, ReceiptService receiptService) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
        this.receiptService = receiptService;
    }

    public BigDecimal calculateTotal(List<OrderItem> itemList){
        // calculates order total including shipping
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal shippingAmount = new BigDecimal("5.99");

        for(OrderItem item : itemList){
            int itemQuantity = item.getQuantity();

            BigDecimal totalItemPrice = item.getPrice().multiply(BigDecimal.valueOf(itemQuantity));
            total = total.add(totalItemPrice);
        }


        return total.add(shippingAmount);
    }

    public List<OrderItem> getCartItems(int userId){
        // gets all items in cart and converts them into an OrderItem object
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        List<OrderItem> orderItems = new ArrayList<>();

        for(ShoppingCartItem item : cart.getItems().values()){

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProduct().getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }



    public int getUserId(Principal principal){
        return profileService.getUserId(principal);
    }

    public Profile getUserProfile(Principal principal){
        return profileService.getProfileById(principal);
    }

    @Transactional
    public Order checkout(Principal principal){
        // in the future when i have more time i plan on breaking down this method into smaller pieces
        // finalizes user's order
        int userId = getUserId(principal);

        List<OrderItem> orderItems = getCartItems(userId);

        // checks if order is empty to keep user from checking out an empty cart
        if (orderItems.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot checkout an empty orderItems");
        }

        Profile userProfile = getUserProfile(principal);

        String userAddress = userProfile.getAddress();

        String userCity = userProfile.getCity();

        String userState = userProfile.getState();

        String userZip = userProfile.getZip();

        BigDecimal total = calculateTotal(orderItems);

        Order newOrder = new Order();


        // sets newOrder to information of current order
        newOrder.setUserId(userId);
        newOrder.setOrderDate(DateUtils.currentDateAndTime());
        newOrder.setItems(orderItems);
        newOrder.setAddress(userAddress);
        newOrder.setCity(userCity);
        newOrder.setState(userState);
        newOrder.setZip(userZip);
        newOrder.setShippingAmount(new BigDecimal("5.99"));
        newOrder.setTotal(total);

        for (OrderItem item : orderItems){
            item.setOrder(newOrder);
        }

        // saves order to repository
        orderRepository.save(newOrder);

        // takes order information to receipt service to create a receipt
        receiptService.saveReceipt(newOrder);

        // clears user's cart after order is finished
        shoppingCartService.clearCart(userId);

        // returns an empty cart
        return newOrder;
    }
}
