#ifndef ADMIN_H
#define ADMIN_H

#include <string>

class Admin
{
public:
    static std::string getAdminCode();
    
    static bool setAdminCode(const std::string& newCode);
    
    static bool changeAdminCode(const std::string& oldCode, const std::string& newCode);
    
    static bool validateAdminCode(const std::string& code);
    
    static bool adminCodeExists();
    
    static bool createDefaultAdminCode();

    static bool isValidAdmin(const std::string& username, const std::string& password);
    static std::string getPassword(const std::string& username);
};

#endif
