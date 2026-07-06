#ifndef PASSWORDVALIDATOR_H
#define PASSWORDVALIDATOR_H

#include <string>

class PasswordValidator
{
public:
    static bool validate(const std::string& password);
    static bool confirmPassword(const std::string& password, const std::string& confirm);
};

#endif
