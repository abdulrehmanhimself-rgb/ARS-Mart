#ifndef USERDATABASE_H
#define USERDATABASE_H

#include "User.h"
#include <string>

class UserDatabase
{
private:
    static std::string getDatabaseFilePath();

public:
    static bool userExists(const User& u);
    static void addUser(User u);
    static bool getUser(std::string username, User& u);
};

#endif
