#include <iostream>
#include <string>
#include <sstream>
#include <cstdlib>
#include <fstream>
#include <vector>
#include <utility>
#include <algorithm>
#include <ctime>

#include "Application.h"
#include "InventoryManager.h"
#include "Admin.h"
#include "UserDatabase.h"
#include "PasswordValidator.h"
#include "ReceiptManager.h"
#include "Globals.h"
#include "HistoryManager.h"

using namespace std;

AuthManager auth;
InventoryManager inventory;

int main(int argc, char* argv[])
{
    // Always reload from disk at process start.
    inventory.loadInventoryFromFile();
    inventory.loadCategoriesFromFile();


    if (argc < 2)
    {
        cout << "INVALID";
        return 1;
    }

    string command = argv[1];

    // ================= ADMIN LOGIN =================
    if (command == "adminlogin")
    {
        if (argc < 5)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string adminCode = argv[2];
        string username = argv[3];
        string password = argv[4];

        // ✅ Validate admin code
        if (!Admin::validateAdminCode(adminCode))
        {
            cout << "INVALID_ADMIN_CODE";
            return 1;
        }

        // ✅ Get correct password for admin
        string correctPass = Admin::getPassword(username);

        if (correctPass == "")
        {
            cout << "ADMIN_NOT_FOUND";
            return 1;
        }

        // ✅ Compare passwords
        if (password == correctPass)
        {
            cout << "ADMIN_SUCCESS";
            return 0;
        }
        else
        {
            cout << "WRONG_PASSWORD";
            return 1;
        }
    }

    // ================= USER LOGIN =================
    else if (command == "userlogin")
    {
        if (argc < 4)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string username = argv[2];
        string password = argv[3];

        User u;

        // ✅ Check if user exists
        if (!UserDatabase::getUser(username, u))
        {
            cout << "USER_NOT_FOUND";
            return 1;
        }

        // ✅ Verify password
        if (password == u.password)
        {
            cout << "USER_SUCCESS";
            return 0;
        }
        else
        {
            cout << "WRONG_PASSWORD";
            return 1;
        }
    }

    // ================= REGISTER =================
    else if (command == "register")
    {
        if (argc < 5)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        User u;
        u.username = argv[2];
        u.email = argv[3];
        u.password = argv[4];

        // ✅ Email validation
        if (u.email.find('@') == string::npos ||
            u.email.find('.') == string::npos)
        {
            cout << "INVALID_EMAIL";
            return 1;
        }

        // ✅ Password validation
        if (u.password.length() < 8)
        {
            cout << "WEAK_PASSWORD";
            return 1;
        }

        bool hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : u.password)
        {
            if (isupper(c)) hasUpper = true;
            if (islower(c)) hasLower = true;
            if (isdigit(c)) hasDigit = true;
        }

        if (!hasUpper || !hasLower || !hasDigit)
        {
            cout << "WEAK_PASSWORD";
            return 1;
        }

        // ✅ Check if user exists
        if (UserDatabase::userExists(u))
        {
            cout << "USER_EXISTS";
            return 1;
        }

        // ✅ Add user to database
        UserDatabase::addUser(u);
        cout << "REGISTER_SUCCESS";
        return 0;
    }

    // ================= VIEW INVENTORY =================
    else if (command == "inventory")
    {
        vector<Inventory>& items = inventory.getItems();

        for (auto& item : items)
        {
            cout << item.name << "|"
                << item.category << "|"
                << item.stock << "|"
                << item.price << "\n";
        }
        return 0;
    }

    // ================= GET ADMIN CODE =================
    else if (command == "getadmincode")
    {
        if (!Admin::adminCodeExists())
        {
            Admin::createDefaultAdminCode();
        }
        // ✅ Return default code (for first-time setup)
        cout << "Admin123";
        return 0;
    }

    // ================= USER CART / CHECKOUT (NON-INTERACTIVE CLI) =================
    else if (command == "cart_add")
    {
        // cart_add <username> <productName> <qty>
        if (argc < 5)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string username = argv[2];
        string productName = argv[3];
        int qty = stoi(argv[4]);

        currentUsername = username;

        auto& items = inventory.getItems();
        for (auto& it : items)
        {
            if (it.name == productName)
            {
                if (qty <= 0)
                {
                    cout << "ERROR|QTY_MUST_BE_POSITIVE";
                    return 1;
                }

                int availableToAdd = it.stock - it.cartQty;
                if (qty > availableToAdd)
                {
                    cout << "ERROR|INSUFFICIENT_STOCK|" << availableToAdd;
                    return 1;
                }

                it.stock -= qty;
                it.cartQty += qty;

                historySystem.logAddToCart(currentUsername, it.name, qty);
                inventory.saveInventoryToFile();

                cout << "OK|" << it.name << "|" << qty << "|" << it.stock << "|" << it.cartQty;

                return 0;
            }
        }

        cout << "ERROR|PRODUCT_NOT_FOUND";
        return 1;
    }
    else if (command == "cart_view")
    {
        // cart_view <username>
        if (argc < 3)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string username = argv[2];
        currentUsername = username;

        auto& items = inventory.getItems();

        double total = 0.0;
        for (auto& it : items)
        {
            if (it.cartQty > 0)
            {
                double lineTotal = it.cartQty * it.price;
                total += lineTotal;
                cout << it.name << "|" << it.category << "|" << it.cartQty << "|" << it.price << "|" << lineTotal << "\n";
            }
        }
        cout << "TOTAL|" << total;
        return 0;
    }
    else if (command == "cart_checkout")
    {
        // cart_checkout <username>
        // Non-interactive: checks out ALL cart items, uses COD pricing (charges=100).
        if (argc < 3)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string username = argv[2];
        currentUsername = username;

        auto& items = inventory.getItems();

        vector<Inventory> selectedItems;
        vector<int> selectedIndexes;
        double subtotal = 0.0;

        for (int i = 0; i < (int)items.size(); i++)
        {
            if (items[i].cartQty > 0)
            {
                selectedIndexes.push_back(i);
                selectedItems.push_back(items[i]);
                subtotal += items[i].cartQty * items[i].price;
            }
        }

        if (selectedItems.empty())
        {
            cout << "ERROR|CART_EMPTY";
            return 1;
        }

        // COD
        double codCharges = 100.0;
        double finalBill = subtotal + codCharges;

        for (int idx : selectedIndexes)
        {
            Inventory& item = items[idx];
            int qty = item.cartQty;
            if (qty > 0)
            {
                double lineTotal = qty * item.price;

                item.stock -= qty;
                salesSystem.recordSale(item.name, qty, lineTotal);
                historySystem.logPurchase(currentUsername, item.name, qty, lineTotal);
                item.cartQty = 0;
            }
        }

        ReceiptManager::generateReceipt(selectedItems, subtotal, codCharges);

        inventory.saveInventoryToFile();
        salesSystem.saveSalesToFile();

        cout << "OK|CHECKOUT_SUCCESS|" << finalBill;
        return 0;
    }

    // ================= ADMIN ACTIONS (NON-INTERACTIVE CLI) =================
    else if (command == "admin_addproduct")
    {
        // admin_addproduct <category> <name> <stock> <price>
        if (argc < 6)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string category = argv[2];
        string name = argv[3];
        int stock = stoi(argv[4]);
        double price = stod(argv[5]);

        if (stock <= 0 || price <= 0.0)
        {
            cout << "ERROR|INVALID_STOCK_OR_PRICE";
            return 1;
        }

        auto& items = inventory.getItems();
        for (auto& it : items)
        {
            if (it.name == name)
            {
                cout << "ERROR|PRODUCT_ALREADY_EXISTS";
                return 1;
            }
        }

        Inventory newItem;
        newItem.name = name;
        newItem.category = category;
        newItem.stock = stock;
        newItem.price = price;
        newItem.cartQty = 0;

        items.push_back(newItem);
        inventory.saveInventoryToFile();

        cout << "OK|" << newItem.name;
        return 0;
    }
    else if (command == "admin_restock")
    {
        // admin_restock <productName> <qty>
        if (argc < 4)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string name = argv[2];
        int qty = stoi(argv[3]);

        if (qty <= 0)
        {
            cout << "ERROR|QTY_MUST_BE_POSITIVE";
            return 1;
        }

        auto& items = inventory.getItems();
        for (auto& it : items)
        {
            if (it.name == name)
            {
                it.stock += qty;
                inventory.saveInventoryToFile();
                cout << "OK|" << it.name << "|" << it.stock;
                return 0;
            }
        }

        cout << "ERROR|PRODUCT_NOT_FOUND";
        return 1;
    }
    else if (command == "admin_updateprice")
    {
        // admin_updateprice <productName> <newPrice>
        if (argc < 4)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string name = argv[2];
        double newPrice = stod(argv[3]);

        if (newPrice <= 0.0)
        {
            cout << "ERROR|INVALID_PRICE";
            return 1;
        }

        auto& items = inventory.getItems();
        for (auto& it : items)
        {
            if (it.name == name)
            {
                it.price = newPrice;
                inventory.saveInventoryToFile();
                cout << "OK|" << it.name << "|" << it.price;
                return 0;
            }
        }

        cout << "ERROR|PRODUCT_NOT_FOUND";
        return 1;
    }

    // ================= UNKNOWN COMMAND =================
    // ================= USER ORDERS / HISTORY (CLI) =================
    else if (command == "history_user")
    {
        // history_user <username>
        if (argc < 3)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        string username = argv[2];

        // historySystem must already be loaded in Application / startup in your flow.
        // For CLI, we simply output from the in-memory history list.
        // If history is empty, backend will output EMPTY.
        // Output format: ACTION|PRODUCT|QTY|AMOUNT|TIMESTAMP
        if (historySystem.getHistoryCount() == 0)
        {
            cout << "EMPTY";
            return 0;
        }

        ifstream file("data/history.txt");
        if (!file.is_open())
        {
            cout << "EMPTY";
            return 0;
        }

        string line;
        bool any = false;
        while (getline(file, line))
        {
            if (line.empty()) continue;

            stringstream ss(line);
            string timestamp, action, u, product, qtyStr, amountStr;

            getline(ss, timestamp, '|');
            getline(ss, action, '|');
            getline(ss, u, '|');
            getline(ss, product, '|');
            getline(ss, qtyStr, '|');
            getline(ss, amountStr);

            if (u == username)
            {
                any = true;
                cout << action << "|" << product << "|" << qtyStr << "|" << amountStr << "|" << timestamp << "\n";
            }
        }

        if (!any) cout << "EMPTY";
        return 0;
    }
    else if (command == "history_all")
    {
        // history_all
        if (argc < 2)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        ifstream file("data/history.txt");
        if (!file.is_open())
        {
            cout << "EMPTY";
            return 0;
        }

        string line;
        bool any = false;
        while (getline(file, line))
        {
            if (line.empty()) continue;

            stringstream ss(line);
            string timestamp, action, u, product, qtyStr, amountStr;

            getline(ss, timestamp, '|');
            getline(ss, action, '|');
            getline(ss, u, '|');
            getline(ss, product, '|');
            getline(ss, qtyStr, '|');
            getline(ss, amountStr);

            any = true;
            cout << action << "|" << product << "|" << qtyStr << "|" << amountStr << "|" << timestamp << "|" << u << "\n";
        }

        if (!any) cout << "EMPTY";
        return 0;
    }

    // ================= SALES REPORT (CLI) =================
    else if (command == "salesreport")
    {
        // salesreport
        if (argc < 2)
        {
            cout << "INVALID_ARGUMENTS";
            return 1;
        }

        ifstream file("data/sales.txt");
        if (!file.is_open())
        {
            cout << "EMPTY";
            return 0;
        }

        string line;
        bool any = false;
        while (getline(file, line))
        {
            if (line.empty()) continue;

            stringstream ss(line);
            string date, product, qtyStr, amountStr;
            getline(ss, date, '|');
            getline(ss, product, '|');
            getline(ss, qtyStr, '|');
            getline(ss, amountStr);

            any = true;
            cout << date << "|" << product << "|" << qtyStr << "|" << amountStr << "\n";
        }

        if (!any) cout << "EMPTY";
        return 0;
    }

    else
    {
        cout << "UNKNOWN_COMMAND";
        return 1;
    }

    return 0;
}


