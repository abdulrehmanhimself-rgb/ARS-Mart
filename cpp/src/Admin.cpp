#include "Admin.h"
#include "PasswordValidator.h"
#include <fstream>
#include <iostream>
#include <cstdlib>

using namespace std;

const string ADMIN_CODE_FILE = "data/admin_code.txt";
const string DEFAULT_CODE = "Admin123"; 

string Admin::getAdminCode()
{
    ifstream file(ADMIN_CODE_FILE.c_str());
    string code;

    if (file.is_open())
    {
        getline(file, code);
        file.close();
        return code;
    }

    return "";
}

bool Admin::setAdminCode(const string& newCode)
{
    ofstream file(ADMIN_CODE_FILE.c_str());

    if (file.is_open())
    {
        file << newCode;
        file.close();
        return true;
    }

    return false;
}

bool Admin::changeAdminCode(const string& oldCode, const string& newCode)
{
    if (newCode.length() < 8)
    {
        cout << "\033[1;31mNew code must be at least 8 characters long!\033[0m\n";
        return false;
    }

    bool hasLetter = false;
    bool hasDigit = false;

    for (char c : newCode)
    {
        if (isalpha(c)) hasLetter = true;
        if (isdigit(c)) hasDigit = true;
    }

    if (!hasLetter || !hasDigit)
    {
        cout << "\033[1;31mNew code must contain at least one letter and one digit!\033[0m\n";
        return false;
    }

    if (oldCode != getAdminCode())
    {
        cout << "\033[1;31mCurrent code is incorrect!\033[0m\n";
        return false;
    }

    if (setAdminCode(newCode))
    {
        cout << "\033[1;32mAdmin code changed successfully!\033[0m\n";
        return true;
    }

    return false;
}

bool Admin::validateAdminCode(const string& code)
{
    string storedCode = getAdminCode();
    return (code == storedCode);
}

bool Admin::adminCodeExists()
{
    ifstream file(ADMIN_CODE_FILE.c_str());
    return file.is_open();
}

bool Admin::createDefaultAdminCode()
{
    return setAdminCode(DEFAULT_CODE);
}

bool Admin::isValidAdmin(const string& username, const string& password)
{
    return (username == "sohail" && password == "sohail123") ||
        (username == "ar" && password == "ar123") ||
        (username == "ahmer" && password == "ahmer123");
}

string Admin::getPassword(const string& username)
{
    if (username == "sohail") return "sohail123";
    if (username == "ar") return "ar123";
    if (username == "ahmer") return "ahmer123";

    return "";
}
