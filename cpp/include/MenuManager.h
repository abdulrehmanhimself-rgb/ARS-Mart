#ifndef MENU_MANAGER_H
#define MENU_MANAGER_H

#include "Cart.h"
#include "Globals.h"
#include "Admin.h"

class MenuManager
{
private:
    CartManager cart;

public:
    void showAdminMenu();
    void showUserMenu();
    
};

#endif
