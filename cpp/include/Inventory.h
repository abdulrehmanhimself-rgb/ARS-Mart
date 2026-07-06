#ifndef INVENTORY_H
#define INVENTORY_H

#include <string>

class Inventory
{
public:
    // Public members for backward compatibility
    std::string name;
    std::string category;
    int stock;
    double price;
    int cartQty;
    int cost;

    Inventory() : name(""), category(""), stock(0), price(0.0), cartQty(0), cost(0) {}

    Inventory(const std::string& n, const std::string& c, int s, double p)
        : name(n), category(c), stock(s), price(p), cartQty(0), cost(0) {}

    // ===== GETTERS (for good practice) =====
    std::string getCategory() const { return category; }
    std::string getName() const { return name; }
    int getStock() const { return stock; }
    double getPrice() const { return price; }
    int getCartQty() const { return cartQty; }
    int getCost() const { return cost; }

    // ===== SETTERS =====
    void setName(const std::string& n) { name = n; }
    void setCategory(const std::string& c) { category = c; }
    void setStock(int s) { stock = s; }
    void setPrice(double p) { price = p; }
    void setCartQty(int q) { cartQty = q; }
    void setCost(int c) { cost = c; }

    // ===== OPERATOR OVERLOADING =====

    // Equality operator
    bool operator==(const Inventory& other) const
    {
        return this->name == other.name && this->category == other.category;
    }

    // Inequality operator
    bool operator!=(const Inventory& other) const
    {
        return !(*this == other);
    }

    // Less than operator (for sorting by price)
    bool operator<(const Inventory& other) const
    {
        return this->price < other.price;
    }

    // Greater than operator
    bool operator>(const Inventory& other) const
    {
        return this->price > other.price;
    }

    // Less than or equal operator
    bool operator<=(const Inventory& other) const
    {
        return this->price <= other.price;
    }

    // Greater than or equal operator
    bool operator>=(const Inventory& other) const
    {
        return this->price >= other.price;
    }

    // Addition operator - add to stock
    Inventory operator+(int quantity) const
    {
        Inventory result = *this;
        result.stock += quantity;
        return result;
    }

    // Subtraction operator - remove from stock
    Inventory operator-(int quantity) const
    {
        Inventory result = *this;
        result.stock = (result.stock > quantity) ? result.stock - quantity : 0;
        return result;
    }

    // Addition assignment operator
    Inventory& operator+=(int quantity)
    {
        this->stock += quantity;
        return *this;
    }

    // Subtraction assignment operator
    Inventory& operator-=(int quantity)
    {
        this->stock = (this->stock > quantity) ? this->stock - quantity : 0;
        return *this;
    }

    // Multiplication operator - calculate total value
    double operator*(int quantity) const
    {
        return this->price * quantity;
    }

    // Pre-increment operator - increase stock by 1
    Inventory& operator++()
    {
        ++this->stock;
        return *this;
    }

    // Post-increment operator
    Inventory operator++(int)
    {
        Inventory temp = *this;
        this->stock++;
        return temp;
    }

    // Pre-decrement operator - decrease stock by 1
    Inventory& operator--()
    {
        if (this->stock > 0) --this->stock;
        return *this;
    }

    // Post-decrement operator
    Inventory operator--(int)
    {
        Inventory temp = *this;
        if (this->stock > 0) this->stock--;
        return temp;
    }

    // Assignment operator (deep copy)
    Inventory& operator=(const Inventory& other)
    {
        if (this != &other)
        {
            this->name = other.name;
            this->category = other.category;
            this->stock = other.stock;
            this->price = other.price;
            this->cartQty = other.cartQty;
            this->cost = other.cost;
        }
        return *this;
    }

    // ===== UTILITY METHODS =====
    void addToCart(int qty)
    {
        if (qty <= stock)
        {
            cartQty += qty;
            stock -= qty;
        }
    }

    void removeFromCart(int qty)
    {
        if (qty <= cartQty)
        {
            cartQty -= qty;
            stock += qty;
        }
    }

    bool hasStock() const
    {
        return stock > 0;
    }

    double getTotalValue() const
    {
        return price * stock;
    }
};

#endif // INVENTORY_H
