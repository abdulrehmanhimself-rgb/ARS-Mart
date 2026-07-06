#include "MenuManager.h"
#include "Utils.h"
#include "Globals.h"
#include <iostream>
#include "InventoryManager.h"
#include "Admin.h"
#include "Cart.h"
#include<limits>

using namespace std;

void MenuManager::showAdminMenu()
{
    int choice;

    do
    {
        Utils::clearScreen();
        Utils::appTheme();

cout << "\033[1;33m1.\033[0m  View Inventory\n";

        cout << "\033[1;33m2.\033[0m  Restock Product\n";

        cout << "\033[1;33m3.\033[0m  Update Product Prices\n";

        cout << "\033[1;33m4.\033[0m  Add New Product\n";

        cout << "\033[1;33m5.\033[0m  Delete Product\n";

        cout << "\033[1;33m6.\033[0m  Search Product\n";

        cout << "\033[1;33m7.\033[0m  Low Stock Warning\n";

        cout << "\033[1;33m8.\033[0m  Show All Categories\n";

        cout << "\033[1;33m9.\033[0m  Add New Category\n";

        cout << "\033[1;33m10.\033[0m Show Full Inventory (Category Wise)\n";

        cout << "\033[1;33m11.\033[0m Delete Category (Safe)\n";

        cout << "\033[1;33m12.\033[0m Category Wise Sales Report\n";

        cout << "\033[1;33m13.\033[0m Daily Sales Report\n";

        cout << "\033[1;33m14.\033[0m Product Wise Profit Report\n";

        cout << "\033[1;33m15.\033[0m View Purchase History\n";

        cout << "\033[1;33m16.\033[0m View User History\n";

        cout << "\033[1;33m17.\033[0m Change Admin Code\n";

        cout << "\033[1;31m0.\033[0m  Log Out\n";

        cout << "\033[1;32mEnter Choice: \033[0m";


        choice = Utils::getIntInput();

        switch (choice)
        {
        case 1:
         inventorySystem.viewInventory();
         break;

        case 2:
         inventorySystem.restockProduct();
         break;

        case 3:
         inventorySystem.updateProductPrice();
         break;

        case 4:
         inventorySystem.addNewProduct();
         break;

        case 5:
         inventorySystem.deleteProduct();
         break;

        case 6:
         inventorySystem.searchProduct();
         break;

        case 7:
         inventorySystem.lowStockWarning();
         break;

        case 8:
         inventorySystem.showAllCategories();
         break;

        case 9:
         inventorySystem.addNewCategory();
         break;

        case 10:
         inventorySystem.showAllStockByCategory();
         break;

        case 11:
        inventorySystem.deleteCategory();
        break;

        case 12:
        {
            auto& items = inventorySystem.getItems();
            auto& categories = inventorySystem.getCategories();
            salesSystem.categoryWiseSalesReport(items, categories);
        }
        break;

        case 13:
        salesSystem.dailySalesReport();
        break;

        case 14:
        {
            auto& items = inventorySystem.getItems();
            salesSystem.productWiseProfitReport(items);
        }
        break;

        case 15:
        historySystem.showHistory();
        break;

        case 16:
        {
            string username;
            cout << "Enter username: ";
            cin >> username;
            historySystem.showUserHistory(username);
        }
        break;

        case 17:
        {
            string oldCode, newCode, confirmCode;
            cout << "\033[1;34m\n=========== CHANGE ADMIN CODE ===========\033[0m\n";
            cout << "Enter current admin code: ";
            cin >> oldCode;
            cout << "Enter new admin code: ";
            cin >> newCode;
            cout << "Confirm new admin code: ";
            cin >> confirmCode;
            
            if (newCode != confirmCode)
            {
                cout << "\033[1;31mCodes do not match!\033[0m\n";
            }
            else if (Admin::changeAdminCode(oldCode, newCode))
            {
                cout << "\033[1;32mAdmin code changed successfully!\033[0m\n";
            }
            else
            {
                cout << "\033[1;31mFailed to change admin code. Check your current code.\033[0m\n";
            }
            Utils::pressEnterToContinue();
        }
        break;

        case 0:
        cout << "Logged out successfully.\n";
        break;

        default:
            cout << "Invalid choice!\n";
            Utils::pressEnterToContinue();
        }

    } while (choice != 0);
}

void MenuManager::showUserMenu()
{
    int choice;

    do
    {
        Utils::clearScreen();
        Utils::appTheme();

        cout << "\033[1;33m1.\033[0m  View Inventory\n";

        cout << "\033[1;33m2.\033[0m  Buy Product (Direct)\n";

        cout << "\033[1;33m3.\033[0m  Add to Cart\n";

        cout << "\033[1;33m4.\033[0m  View Cart\n";

        cout << "\033[1;33m5.\033[0m  Remove From Cart\n";

        cout << "\033[1;33m6.\033[0m  Checkout Cart\n"; 

        cout << "\033[1;33m7.\033[0m  Show All Categories\n";

        cout << "\033[1;33m8.\033[0m  Show Full Inventory (Category Wise)\n"; 

        cout << "\033[1;31m0.\033[0m  Logout\n";

        cout << "\033[1;32mEnter Choice: \033[0m";

        choice = Utils::getIntInput();

        switch (choice)
        {
        case 1:
        inventorySystem.viewInventory(); 
        break;

        case 2: 
        cart.buyProduct(); 
        break;

        case 3: 
        cart.addToCart(); 
        break;

        case 4:
            cart.viewCart();
            break;

        case 5:
            cart.removeFromCart();
            break;

        case 6:
            cart.checkoutCart();
            break;

        case 7:
            inventorySystem.showAllCategories();
            break;

        case 8: 
            inventorySystem.showAllStockByCategory();
            break;


        case 0:
        {
            auto& items = inventorySystem.getItems();
            bool hasItemsInCart = false;

            for (const auto& item : items)
            {
                if (item.cartQty > 0)
                {
                    hasItemsInCart = true;
                    break;
                }
            }

            if (hasItemsInCart)
            {
                Utils::clearScreen();
                Utils::appTheme();

                cout << "\033[1;31m\n========== CANNOT LOGOUT ==========\033[0m\n";
                cout << "\033[1;33mYour cart contains items!\033[0m\n\n";
                cout << "\033[1;36mYou must either:\033[0m\n";
                cout << "\033[1;33m1. Remove all items from cart (Option 5 from main menu)\033[0m\n";
                cout << "\033[1;32m2. Checkout your cart (Option 6 from main menu)\033[0m\n";
            
                cout << "\033[1;35m====================================\033[0m\n";
                
                cout << "\033[1;33m\nPress Enter to continue...\033[0m\n";
                cin.ignore(numeric_limits<streamsize>::max(), '\n');

                cin.get();
                showUserMenu();
            }
            else
            {
                cout << "Logged out successfully.\n";
                break;
            }
        }

        default:
            cout << "Invalid choice!\n";
            Utils::pressEnterToContinue();
        }
        
    } while (choice != 0);
}
