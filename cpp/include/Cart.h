// Cart.h - ENHANCED WITH OOP CONCEPTS
#ifndef CART_H
#define CART_H

#include "Inventory.h"
#include "InventoryManager.h"
#include <vector>

// Cart Item helper structure with operator overloading
struct CartItem
{
    Inventory item;
    int quantity;

    CartItem() : item(), quantity(0) {}
    CartItem(const Inventory& inv, int qty) : item(inv), quantity(qty) {}

    // Operator overloading for CartItem
    double operator*() const { return item.price * quantity; }  // Get total price

    bool operator==(const CartItem& other) const
    {
        return item.name == other.item.name;
    }

    bool operator!=(const CartItem& other) const
    {
        return !(*this == other);
    }

    CartItem& operator+=(int qty)
    {
        quantity += qty;
        return *this;
    }

    CartItem& operator-=(int qty)
    {
        quantity = (quantity > qty) ? quantity - qty : 0;
        return *this;
    }
};

// Main CartManager class with enhanced OOP
class CartManager
{
private:
    std::vector<CartItem> cartItems;
    double totalBill;
    double codCharges;

    bool processPayment(double bill, double& codCharges);
    void displayCartItems(double& total);

public:
    CartManager() : totalBill(0.0), codCharges(0.0) {}

    // Original methods
    void addToCart();
    void viewCart();
    void removeFromCart();
    void checkoutCart();
    void buyProduct();

    // ===== NEW OPERATOR OVERLOADING =====

    // Get total items in cart
    int operator()() const { return cartItems.size(); }

    // Access cart item by index
    CartItem& operator[](int index) { return cartItems[index]; }
    const CartItem& operator[](int index) const { return cartItems[index]; }

    // Add item to cart using +=
    CartManager& operator+=(const CartItem& item)
    {
        for (auto& existingItem : cartItems)
        {
            if (existingItem == item)
            {
                existingItem += item.quantity;
                return *this;
            }
        }
        cartItems.push_back(item);
        return *this;
    }

    // Remove item from cart using -=
    CartManager& operator-=(const CartItem& item)
    {
        for (auto it = cartItems.begin(); it != cartItems.end(); ++it)
        {
            if (*it == item)
            {
                cartItems.erase(it);
                break;
            }
        }
        return *this;
    }

    // Calculate total bill
    double operator+() const
    {
        double total = 0.0;
        for (const auto& item : cartItems)
        {
            total += *item;  // Uses operator* overload
        }
        return total;
    }

    // ===== TEMPLATE METHODS FOR CART OPERATIONS =====

    template<typename Predicate>
    int countItemsIf(Predicate pred) const
    {
        int count = 0;
        for (const auto& item : cartItems)
        {
            if (pred(item))
                count++;
        }
        return count;
    }

    template<typename Predicate>
    std::vector<CartItem> filterCart(Predicate pred) const
    {
        std::vector<CartItem> filtered;
        for (const auto& item : cartItems)
        {
            if (pred(item))
                filtered.push_back(item);
        }
        return filtered;
    }

    template<typename Function>
    void forEachItem(Function func)
    {
        for (auto& item : cartItems)
        {
            func(item);
        }
    }

    // Utility methods
    bool isEmpty() const { return cartItems.empty(); }
    void clear() { cartItems.clear(); totalBill = 0.0; }
    std::vector<CartItem>& getItems() { return cartItems; }
    const std::vector<CartItem>& getItems() const { return cartItems; }
};

#endif // CART_H
