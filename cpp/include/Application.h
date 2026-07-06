#ifndef APPLICATION_H
#define APPLICATION_H

#include "AuthManager.h"
#include "MenuManager.h"
#include "InventoryManager.h"

class Application
{
private:
    AuthManager auth;
    MenuManager menu;

    void showMainMenu();
    void handleUserSection();

public:
    Application();
    void run();
};

#endif