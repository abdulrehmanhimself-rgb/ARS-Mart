#ifndef AUTHMANAGER_H
#define AUTHMANAGER_H

#include <vector>
#include <string>
#include "User.h"
#include "Manager.h"

class AuthManager : public Manager
{
private:
    std::vector<User*> users; 
    std::string role;
    std::string currentUsername;

    bool login(std::string correctPass);
    bool verifyAdminCode();

public:
    AuthManager();
    ~AuthManager();  

    void initialize() override;
    void save() override;
    void load() override;
    std::string getStatus() const override;

    void loadUsers();
    bool registerUser();
    bool userLogin();
    bool adminLogin();
    void forgotPassword();
    bool isValidEmail(std::string email);
    std::string getCurrentUsername() const { return currentUsername; }
    std::string getCurrentRole() const { return role; }
    void logout() { currentUsername = ""; role = ""; }

    template<typename Predicate>
    User* findUser(Predicate pred)
    {
        for (auto& user : users)
        {
            if (user && pred(*user))
                return user;
        }
        return nullptr;
    }

    template<typename Predicate>
    int countUsersIf(Predicate pred)
    {
        int count = 0;
        for (const auto& user : users)
        {
            if (user && pred(*user))
                count++;
        }
        return count;
    }

    size_t getTotalUsers() const { return users.size(); }
};

#endif
