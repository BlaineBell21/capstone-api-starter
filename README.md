╔══════════════════════════════════════════════╗

           🌙 MOONBEAM MARKET

       Games • Gear • Digital Treasures

      Browse the cosmos for your next adventure.

╚══════════════════════════════════════════════╝

<p align="center">

<img src="images/logo.png" width="350">

### *Games • Gear • Digital Treasures*

*A celestial-themed full-stack video game storefront built with Java, Spring Boot, MySQL, and JavaScript.*

</p>

---

## ✨ About The Project

Moonbeam Market is a full-stack e-commerce application inspired by modern digital game storefronts. Originally built from a classroom web store project, it has been extensively redesigned with custom branding, a completely refreshed user interface, additional backend functionality, and new features to create a more polished user experience.

The goal of this project was not only to build a functional REST API, but also to explore full-stack application architecture, authentication, shopping cart management, checkout workflows, and frontend design.

---

## 📸 Screenshots

| Home                      | Shopping Cart             |
| ------------------------- | ------------------------- |
| ![](images/home.png) | ![](images/cart.png) |

| Profile                      | Login                      |
| ---------------------------- | -------------------------- |
| ![](images/profile.png) | ![](images/login.png) |

| Checkout                      | Admin Dashboard *(Coming Soon)* |
| ----------------------------- |---------------------------------|
| ![](images/checkout.png) | ![](images/admin.png)              |

---

# ✨ Features

## 🛍 Customer Features

* Browse products by category
* Product filtering
* Shopping cart
* Update cart quantities
* Secure checkout
* Receipt generation
* Customer profile management
* JWT Authentication
* Responsive UI
* Animated celestial theme

---

## 👑 Admin Features *(In Progress)*

* Product Management
* Category Management
* Inventory Updates
* Admin Dashboard
* Order Management

---

## 🎨 UI Improvements

Moonbeam Market has been completely redesigned with a custom visual identity including:

* 🌙 Moonbeam branding
* ✨ Animated starfield background
* 💜 Custom purple & cyan color palette
* 🎮 Modern product cards
* 🛒 Redesigned shopping cart
* 👤 Premium profile page
* 🌌 Glassmorphism inspired interface
* 📱 Responsive layout

---

## 💡 Featured Code

One of the most interesting pieces of logic in Moonbeam Market is the checkout workflow implemented in the `OrderService`.

During checkout, the service coordinates several independent parts of the application into a single transaction:

* Retrieves the authenticated user's shopping cart
* Converts `ShoppingCartItem` objects into persistent `OrderItem` entities
* Calculates the order total using `BigDecimal` for accurate monetary calculations
* Creates and saves the `Order`
* Associates each `OrderItem` with the new order
* Clears the user's shopping cart after a successful purchase

This service demonstrates the responsibility of the service layer in a layered Spring Boot application. Rather than simply performing CRUD operations, it encapsulates the application's business rules by validating the cart, transforming data between models, calculating totals, persisting related entities, and maintaining transactional integrity.

There are definitely still some changes/improvements I'd like to make to this method in the future. Currently, it handles validating the cart, creating the order, getting the total, and clearing the cart, and it'd be cleaner to separate all these functions into smaller methods.

```java
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
```

I chose this example because it demonstrates several concepts working together rather than a single isolated feature. It highlights service-layer design, entity relationships, transactional operations, object transformation, and financial calculations using `BigDecimal`, making it one of the core pieces of business logic in the application.


# 🛠 Tech Stack

## Backend

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* MySQL
* Maven

---

## Frontend

* HTML5
* CSS3
* JavaScript
* Axios
* Bootstrap

---

## Development Tools

* IntelliJ IDEA
* MySQL Workbench
* Git
* GitHub
* Insomnia

---

# 🏗 Architecture

```
Frontend (HTML / CSS / JavaScript)

            │

            ▼

REST Controllers

            │

            ▼

Service Layer

            │

            ▼

Repository Layer

            │

            ▼

MySQL Database
```

---

# 🔐 Authentication

Moonbeam Market uses JSON Web Tokens (JWT) to authenticate users.

Features include:

* Secure login
* Role-based authorization
* Customer accounts
* Administrator accounts
* Protected API endpoints

---

# 🗃 Database

Current entities include:

* Users
* Profiles
* Categories
* Products
* Shopping Cart
* Orders
* Order Items

---

# 🚀 Future Improvements

Planned features include:

* ⭐ Wishlist
* 🎮 Featured games section
* 📝 Customer reviews
* 📦 Order history
* 👤 Avatar uploads
* 📧 Email confirmation
* 📊 Sales analytics
* 🛠 Complete admin dashboard
* 🐳 Docker deployment

---

# 📚 What I Learned

Building Moonbeam Market strengthened my understanding of:

* RESTful API development
* Spring Boot architecture
* Layered application design
* Spring Security & JWT Authentication
* Entity relationships with JPA/Hibernate
* BigDecimal for financial calculations
* Shopping cart & checkout workflows
* Responsive frontend development
* UI/UX design principles
* Full-stack debugging and troubleshooting

---

# 🚀 Getting Started

Clone the repository

```bash
git clone https://github.com/BlaineBell21/MoonBeamMarketAPI.git
git clone https://github.com/BlaineBell21/MoonBeamMarket.git 
```

Configure MySQL

```
application.properties
```

Run the Spring Boot application

```
localhost:8080
```

Open the frontend

```
index.html
```

Login

Browse Moonbeam Market!

---

# 🌙 Project Status

🚧 Active Development

Moonbeam Market continues to evolve with new features, UI improvements, and administrative functionality.

---

<p align="center">

### Thanks for visiting Moonbeam Market! 🌙✨

</p>
