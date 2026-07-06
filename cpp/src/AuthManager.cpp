#include "AuthManager.h"
#include "SecurityManager.h"
#include "Utils.h"
#include "Globals.h"
#include "Admin.h"
#include "UserDatabase.h"
#include "PasswordValidator.h"

#include <iostream>
#include <limits>

using namespace std;

AuthManager::AuthManager()
{
    loadUsers();
}

void AuthManager::loadUsers()
{
    users.clear();
}

bool AuthManager::login(string correctPass)
{
    string pass;

    while (true)
    {
        pass = Utils::getStringInput("\033[1;36mEnter Password: \033[0m");
        
        if (pass == "0")
            return false;

        if (PasswordValidator::confirmPassword(pass, correctPass))
        {
            return true;
        }
    }
}


bool AuthManager::verifyAdminCode()
{
    if (!Admin::adminCodeExists())
    {
        Admin::createDefaultAdminCode();
    }

    SecurityManager::lockSystem(3);

    string code;
    int attempts = 0;
    const int MAX_ATTEMPTS = 3;

    while (attempts < MAX_ATTEMPTS)
    {
        Utils::clearScreen();
        Utils::appTheme();

        cout << "\033[1;34m\n=========== ADMIN SECURITY CODE ===========\033[0m\n";
        cout << "\033[1;33mEnter your 8-digit admin code: \033[0m";
        
        code = Utils::getStringInput("");

        if (Admin::validateAdminCode(code))
        {
            cout << "\033[1;32mCode verified successfully!\033[0m\n";
            return true;
        }
        else
        {
            attempts++;
            cout << "\033[1;31mIncorrect code! Attempts remaining: " << (MAX_ATTEMPTS - attempts) << "\033[0m\n";
            
            if (attempts < MAX_ATTEMPTS)
            {
                SecurityManager::lockSystem(5);
            }
        }
    }

    cout << "\033[1;31mToo many failed attempts! Access denied.\033[0m\n";
    return false;
}


bool AuthManager::adminLogin()
{
    if (!verifyAdminCode())
    {
        cout << "\033[1;31mAdmin code verification failed!\033[0m\n";
        return false;
    }

    Utils::clearScreen();
    Utils::appTheme();

    string username;

    cout << "\033[1;34m\n=========== ADMIN LOGIN ===========\033[0m\n";

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "Enter Username: ";
    getline(cin, username);

    string correctPass = Admin::getPassword(username);

    if (correctPass == "")
    {
        cout << "\033[1;31mAdmin not found!\033[0m\n";
        return false;
    }

    if (login(correctPass))
    {
        role = "ADMIN";
        currentUsername = username;
        ::currentUsername = username; 
        return true;
    }

    return false;
}


bool AuthManager::userLogin()
{
    Utils::clearScreen();
    Utils::appTheme();

    string username;
    User u;

    cout << "\033[1;34m\n=========== USER LOGIN ===========\033[0m\n";

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "Enter Username: ";
    getline(cin, username);

    if (!UserDatabase::getUser(username, u))
    {
        cout << "\033[1;31mUser not found!\033[0m\n";
        return false;
    }

    if (login(u.password))
    {
        role = "USER";
        currentUsername = username;
        ::currentUsername = username; 
        return true;
    }

    return false;
}


bool AuthManager::registerUser()
{
    Utils::clearScreen();
    Utils::appTheme();

    User u;

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "\033[1;36m\n=========== REGISTER USER ===========\033[0m\n";

    cout << "Enter Username: ";
    getline(cin, u.username);

    
    while (true)
    {
        cout << "Enter Email: ";
        getline(cin, u.email);
        
        if (isValidEmail(u.email))
        break;
        
        cout << "\033[1;31mInvalid Email!\033[0m\n";
    }
    
    string confirm;
    
    while (true)
    {
        cout << "Enter Password: ";
        getline(cin, u.password);
        
        if (!PasswordValidator::validate(u.password))
        continue;
        
        if (UserDatabase::userExists(u))
        {
            cout << "\033[1;31mUser already exists!\033[0m\n";
            return false;
        }

        cout << "Confirm Password: ";
        getline(cin, confirm);

        if (!PasswordValidator::confirmPassword(u.password, confirm))
            continue;

        break;
    }
    UserDatabase::addUser(u);

    cout << "\033[1;32mUser Registered Successfully!\033[0m\n";

    return true;
}

void AuthManager::forgotPassword()
{
    Utils::clearScreen();
    Utils::appTheme();

    string username;
    User u;

    cout << "\033[1;34m\n=========== FORGOT PASSWORD ===========\033[0m\n";

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "Enter Username: ";
    getline(cin, username);

    if (!UserDatabase::getUser(username, u))
    {
        cout << "\033[1;31mUser not found!\033[0m\n";
    }
    else
    {
        cout << "\033[1;32mPassword reset link has been sent to your email.\033[0m\n";
        cout << "\033[1;33m(In production, this would send a secure email)\033[0m\n";
    }

    Utils::pressEnterToContinue();
}

bool AuthManager::isValidEmail(string email)
{
    return (email.find('@') != string::npos &&
        email.find('.') != string::npos);
}

AuthManager::~AuthManager()
{
    for (auto user : users)
    {
        delete user;
    }
    users.clear();
}

void AuthManager::initialize()
{
    loadUsers();
    isInitialized = true;
}

void AuthManager::save()
{
    // 
}

void AuthManager::load()
{
    loadUsers();
}

std::string AuthManager::getStatus() const
{
    return "AuthManager - Total Users: " + std::to_string(users.size()) +
           ", Current User: " + (currentUsername.empty() ? "None" : currentUsername);
}
