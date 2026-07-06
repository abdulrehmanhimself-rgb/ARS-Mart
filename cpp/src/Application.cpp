
#include "Utils.h"
#include "Application.h"
#include "Globals.h"
#include "AuthManager.h"
#include <ctime>
#include <cstdlib>
#include <iostream>

using namespace std;

Application::Application()
{
    srand(time(NULL));
    ::inventorySystem.loadInventoryFromFile();
    ::inventorySystem.loadCategoriesFromFile();
    ::historySystem.loadHistoryFromFile(); 
    Utils::appTheme();
}

void Application::run()
{ 
    showMainMenu();
}

void Application::showMainMenu()
{
    int ch;
    do
    {
        Utils::clearScreen();
        Utils::appTheme();
        cout << "\033[1;33m\n1. Admin\033[0m\n";

        cout << "\033[1;32m2. User\033[0m\n";

        cout << "\033[1;31m0. Exit Program\033[0m\n";

        cout << "\033[1;35mEnter Your Choice: \033[0m";

        ch = Utils::getIntInput();

        switch (ch)
        {
        case 1:
            if (auth.adminLogin())
                menu.showAdminMenu();
            break;

        case 2:
            handleUserSection();
            break;

        case 0:
            cout << "Program Exited\n";
            break;

        default:
            cout << "Invalid Choice!\n";
            Utils::pressEnterToContinue();
        }
    } while (ch != 0);
}

void Application::handleUserSection()
{
    int choice;

    do
    {
        Utils::clearScreen();
        Utils::appTheme();

                cout << "\n\033[1;34m1. Login as user\033[0m\n";

                cout << "\033[1;32m2. Register as user\033[0m\n";

                cout << "\033[1;33m3. Forgot Password\033[0m\n";

                cout << "\033[1;31m0. Go back\033[0m\n";

                cout << "\033[1;35mEnter Your Choice: \033[0m";

        choice = Utils::getIntInput();

        switch (choice)
        {
        case 1:
            if (auth.userLogin())
                menu.showUserMenu();
            break;

        case 2:
            if (auth.registerUser())
                menu.showUserMenu();
            break;

        case 3:
            auth.forgotPassword();
            break;

        case 0:
            break;

        default:
            cout << "Invalid Choice!\n";
            Utils::pressEnterToContinue();
        }
    } while (choice != 0);
}
