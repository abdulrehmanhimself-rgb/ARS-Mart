#include "PasswordValidator.h"
#include <iostream>
#include <cctype>

using namespace std;

bool PasswordValidator::validate(const string& password)
{
    bool hasUpper = false;
    bool hasLower = false;
    bool hasDigit = false;
    bool hasSpecial = false;

    if (password.length() < 8)
    {
        cout << "\033[1;31mPassword must be at least 8 characters long!\033[0m\n";
        return false;
    }

    for (char c : password)
    {
        if (isupper(c))
            hasUpper = true;
        else if (islower(c))
            hasLower = true;
        else if (isdigit(c))
            hasDigit = true;
        else
            hasSpecial = true;
    }

    if (!hasUpper)
    {
        cout << "\033[1;31mPassword must contain at least 1 uppercase letter!\033[0m\n";
        return false;
    }

    if (!hasLower)
    {
        cout << "\033[1;31mPassword must contain at least 1 lowercase letter!\033[0m\n";
        return false;
    }

    if (!hasDigit)
    {
        cout << "\033[1;31mPassword must contain at least 1 digit!\033[0m\n";
        return false;
    }

    if (!hasSpecial)
    {
        cout << "\033[1;31mPassword must contain at least 1 special character!\033[0m\n";
        return false;
    }

    return true;
}

bool PasswordValidator::confirmPassword(const string& password, const string& confirm)
{
    if (password != confirm)
    {
        cout << "\033[1;31mPasswords do not match!\033[0m\n";
        return false;
    }

    return true;
}