#ifndef USER_H
#define USER_H

#include <string>
#include <ctime>
#include <iostream>


class User
{
public:
    std::string username;
    std::string email;
    std::string password;
    std::string userRole;
    time_t registrationDate;

public:
    User() : username(""), email(""), password(""), userRole("USER"), registrationDate(0) {}

    User(const std::string& u, const std::string& e, const std::string& p, const std::string& role = "USER")
        : username(u), email(e), password(p), userRole(role), registrationDate(time(nullptr)) {}
    
    virtual ~User() = default;

    virtual std::string getUsername() const { return username; }
    virtual std::string getEmail() const { return email; }
    virtual std::string getPassword() const { return password; }
    virtual std::string getUserRole() const { return userRole; }
    virtual time_t getRegistrationDate() const { return registrationDate; }

    virtual void setUsername(const std::string& u) { username = u; }
    virtual void setEmail(const std::string& e) { email = e; }
    virtual void setPassword(const std::string& p) { password = p; }

    virtual bool validateCredentials(const std::string& pwd) const
    {
        return password == pwd;
    }

    virtual std::string serialize() const
    {
        return username + "|" + email + "|" + password + "|" + userRole;
    }

    virtual void display() const
    {
        std::cout << "Username: " << username << ", Email: " << email << 
                     ", Role: " << userRole << "\n";
    }

    // Operator overloading for user comparison
    bool operator==(const User& other) const
    {
        return this->username == other.username;
    }

    bool operator!=(const User& other) const
    {
        return !(*this == other);
    }

    bool operator<(const User& other) const
    {
        return this->username < other.username;
    }

    bool operator>(const User& other) const
    {
        return this->username > other.username;
    }
};

class Customer : public User
{
private:
    int totalPurchases;
    double totalSpent;
    std::string lastPurchaseDate;

public:
    Customer() : User("", "", "", "CUSTOMER"), totalPurchases(0), totalSpent(0.0), lastPurchaseDate("") {}

    Customer(const std::string& u, const std::string& e, const std::string& p)
        : User(u, e, p, "CUSTOMER"), totalPurchases(0), totalSpent(0.0), lastPurchaseDate("") {}

    int getTotalPurchases() const { return totalPurchases; }
    double getTotalSpent() const { return totalSpent; }
    std::string getLastPurchaseDate() const { return lastPurchaseDate; }

    void addPurchase(double amount)
    {
        totalPurchases++;
        totalSpent += amount;
        lastPurchaseDate = __TIME__;
    }

    bool validateCredentials(const std::string& pwd) const override
    {
        return password == pwd;
    }

    std::string serialize() const override
    {
        return username + "|" + email + "|" + password + "|CUSTOMER|" +
               std::to_string(totalPurchases) + "|" + std::to_string(totalSpent);
    }

    void display() const override
    {
        std::cout << "Username: " << username << ", Email: " << email << 
                     ", Role: CUSTOMER, Purchases: " << totalPurchases << "\n";
    }
};

class AdminUser : public User
{
private:
    bool hasFullAccess;
    std::string adminLevel;

public:
    AdminUser() : User("", "", "", "ADMIN"), hasFullAccess(false), adminLevel("") {}

    AdminUser(const std::string& u, const std::string& e, const std::string& p, const std::string& level = "BASIC")
        : User(u, e, p, "ADMIN"), hasFullAccess(true), adminLevel(level) {}

    bool getHasFullAccess() const { return hasFullAccess; }
    std::string getAdminLevel() const { return adminLevel; }
    void setAdminLevel(const std::string& level) { adminLevel = level; }

    bool validateCredentials(const std::string& pwd) const override
    {
        return password == pwd;
    }

    std::string serialize() const override
    {
        return username + "|" + email + "|" + password + "|ADMIN|" + adminLevel;
    }

    void display() const override
    {
        std::cout << "Username: " << username << ", Email: " << email << 
                     ", Role: ADMIN, Level: " << adminLevel << "\n";
    }
};

#endif 
