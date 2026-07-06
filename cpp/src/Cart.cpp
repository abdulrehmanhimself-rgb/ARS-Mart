#include "Cart.h"
#include "Inventory.h"
#include "InventoryManager.h"
#include "Globals.h"
#include "ReceiptManager.h"
#include "Utils.h"
#include "SalesManager.h"
#include "HistoryManager.h"

#include <iostream>
#include <iomanip>
#include <vector>
#include <string>

using namespace std;

namespace
{
    void printSeparator(int width = 74)
    {
        cout << "\033[1;34m";
        for (int i = 0; i < width; i++) cout << "=";
        cout << "\033[0m\n";
    }

    void printSingleProductBox(const Inventory& item, int qty, const string& title)
    {
        double subtotal = qty * item.price;

        cout << "\n\033[1;45m\033[1;97m" << title << "\033[0m\n";
        printSeparator(60);

        cout << "\033[1;36m| Product  : \033[0m" << item.name << "\n";
        cout << "\033[1;36m| Quantity : \033[0m" << qty << "\n";
        cout << "\033[1;36m| Price    : \033[0mRs. " << fixed << setprecision(2) << item.price << "\n";
        cout << "\033[1;36m| Subtotal : \033[0mRs. " << fixed << setprecision(2) << subtotal << "\n";

        printSeparator(60);
    }

    double printItemsTable(const vector<Inventory>& productList,
                           const string& title,
                           bool showIndex = false,
                           double extraCharges = 0.0)
    {
        if (!title.empty())
        {
            cout << "\033[1;44m\033[1;97m" << title << "\033[0m\n\n";
        }

        if (productList.empty())
        {
            cout << "\033[1;33mYour cart is empty!\033[0m\n";
            return 0.0;
        }

        int totalQty = 0;
        double subtotal = 0.0;

        cout << left << fixed << setprecision(2);

        printSeparator(showIndex ? 82 : 76);

        if (showIndex)
            cout << setw(6) << "No.";

        cout << setw(28) << "Product"
             << setw(12) << "Quantity"
             << setw(14) << "Price"
             << setw(16) << "Subtotal" << "\n";

        printSeparator(showIndex ? 82 : 76);

        int serial = 1;
        for (const auto& item : productList)
        {
            double lineTotal = item.cartQty * item.price;

            if (showIndex)
                cout << setw(6) << serial++;

            cout << setw(28) << item.name
                 << setw(12) << item.cartQty
                 << setw(14) << item.price
                 << setw(16) << lineTotal << "\n";

            totalQty += item.cartQty;
            subtotal += lineTotal;
        }

        printSeparator(showIndex ? 82 : 76);

        cout << "\033[1;35mTotal Products : \033[0m" << productList.size() << "\n";
        cout << "\033[1;35mTotal Quantity : \033[0m" << totalQty << "\n";
        cout << "\033[1;36mSubtotal       : \033[0mRs. " << subtotal << "\n";

        if (extraCharges > 0)
        {
            cout << "\033[1;33mExtra Charges  : \033[0mRs. " << extraCharges << "\n";
        }

        cout << "\033[1;32mGrand Total    : \033[0mRs. " << (subtotal + extraCharges) << "\n\n";

        return subtotal;
    }

    vector<Inventory> getCartItems(vector<Inventory>& items, vector<int>* indexMap = nullptr)
    {
        vector<Inventory> cartItems;

        if (indexMap)
            indexMap->clear();

        for (int i = 0; i < (int)items.size(); i++)
        {
            if (items[i].cartQty > 0)
            {
                cartItems.push_back(items[i]);

                if (indexMap)
                    indexMap->push_back(i);
            }
        }

        return cartItems;
    }

    void showCategoryProducts(const vector<Inventory>& items, const string& category, vector<int>& indexMap)
    {
        indexMap.clear();

        cout << "\033[1;46m\033[1;30m Products in " << category << " \033[0m\n\n";

        cout << left
             << setw(6)  << "No."
             << setw(28) << "Product"
             << setw(12) << "Stock"
             << setw(12) << "Price" << "\n";

        printSeparator(60);

        int count = 1;
        for (int i = 0; i < (int)items.size(); i++)
        {
            if (items[i].category == category)
            {
                indexMap.push_back(i);

                string stockColor = (items[i].stock <= 5) ? "\033[1;31m" : "\033[1;32m";

                cout << setw(6) << count++
                     << setw(28) << items[i].name
                     << stockColor << setw(12) << items[i].stock << "\033[0m"
                     << setw(12) << items[i].price << "\n";
            }
        }

        printSeparator(60);
    }
}

void CartManager::displayCartItems(double& total)
{
    auto& items = inventorySystem.getItems();
    vector<Inventory> cartItems = getCartItems(items);

    total = printItemsTable(cartItems, "====================== YOUR CART ======================");
}

void CartManager::addToCart()
{
    auto& items = inventorySystem.getItems();

    while (true)
    {
        Utils::clearScreen();
        Utils::appTheme();

        cout << "\033[1;35m================= ADD TO CART =================\033[0m\n\n";

        inventorySystem.showCategories();

        int catChoice = Utils::getIntInput();

        if (catChoice == 0)
            return;

        string category = inventorySystem.getCategoryByChoice(catChoice);

        if (category == "")
        {
            cout << "\033[1;31mInvalid category!\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }

        Utils::clearScreen();
        Utils::appTheme();

        vector<int> indexMap;
        showCategoryProducts(items, category, indexMap);

        if (indexMap.empty())
        {
            cout << "\033[1;31mNo products found!\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }

        cout << "\n\033[1;36mSelect Product Number: \033[0m";
        int choice = Utils::getIntInput();

        if (choice < 1 || choice > (int)indexMap.size())
        {
            cout << "\033[1;31mInvalid selection!\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }

        Inventory& p = items[indexMap[choice - 1]];

        cout << "\033[1;36mEnter Quantity: \033[0m";
        int qty = Utils::getIntInput();

        if (qty <= 0)
        {
            cout << "\033[1;31mQuantity must be positive!\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }

        int availableToAdd = p.stock - p.cartQty;

        if (qty > availableToAdd)
        {
            cout << "\033[1;31mInsufficient stock!\033[0m\n";
            cout << "\033[1;36mAvailable: " << availableToAdd << "\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }

        p.stock -= qty;
        p.cartQty += qty;

        ::historySystem.logAddToCart(::currentUsername, p.name, qty);
        inventorySystem.saveInventoryToFile();

        Utils::clearScreen();
        Utils::appTheme();

        cout << "\033[1;32mAdded to cart successfully!\033[0m\n";

        printSingleProductBox(p, qty, "================ ADDED PRODUCT ================");

        double cartTotal = 0;
        displayCartItems(cartTotal);

        Utils::pressEnterToContinue();

        cout << "\nAdd more? (y/n): ";
        if (Utils::getYesNoInput() == 'n')
            return;
    }
}

void CartManager::viewCart()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m====================== VIEW CART ======================\033[0m\n\n";

    double total = 0;
    displayCartItems(total);

    Utils::pressEnterToContinue();
}

void CartManager::checkoutCart()
{
    Utils::clearScreen();
    Utils::appTheme();

    auto& items = inventorySystem.getItems();

    vector<int> cartIndexMap;
    vector<Inventory> cartItems = getCartItems(items, &cartIndexMap);

    if (cartItems.empty())
    {
        cout << "\033[1;31mCart is empty!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    printItemsTable(cartItems, "==================== CHECKOUT CART ====================", true);

    cout << "\033[1;33mEnter product number to buy from cart (0 for all): \033[0m";
    int selectedChoice = Utils::getIntInput();

    vector<Inventory> selectedItems;
    vector<int> selectedIndexes;

    if (selectedChoice == 0)
    {
        selectedItems = cartItems;
        selectedIndexes = cartIndexMap;
    }
    else if (selectedChoice >= 1 && selectedChoice <= (int)cartItems.size())
    {
        selectedItems.push_back(cartItems[selectedChoice - 1]);
        selectedIndexes.push_back(cartIndexMap[selectedChoice - 1]);
    }
    else
    {
        cout << "\033[1;31mInvalid selection!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    Utils::clearScreen();
    Utils::appTheme();

    double totalBill = printItemsTable(selectedItems, "=================== CHECKOUT SUMMARY ==================");

    double codCharges = 0;

    if (!processPayment(totalBill, codCharges))
    {
        cout << "\033[1;31mPayment Failed!\033[0m\n";

        vector<string> productNames;
        for (const auto& item : selectedItems)
        {
            productNames.push_back(item.name);
        }

        ::historySystem.logCancelPayment(::currentUsername, productNames, totalBill);
        Utils::pressEnterToContinue();
        return;
    }

    for (int idx : selectedIndexes)
    {
        Inventory& item = items[idx];

        if (item.cartQty > 0)
        {
            int qty = item.cartQty;
            double lineTotal = qty * item.price;

            item.stock -= qty;
            salesSystem.recordSale(item.name, qty, lineTotal);
            ::historySystem.logPurchase(::currentUsername, item.name, qty, lineTotal);
            item.cartQty = 0;
        }
    }

    ReceiptManager::generateReceipt(selectedItems, totalBill, codCharges);

    inventorySystem.saveInventoryToFile();
    salesSystem.saveSalesToFile();

    cout << "\033[1;32m\nCheckout Successful!\033[0m\n\n";
    printItemsTable(selectedItems, "====================== FINAL BILL =====================", false, codCharges);

    Utils::pressEnterToContinue();
}

void CartManager::removeFromCart()
{
    Utils::clearScreen();
    Utils::appTheme();

    auto& items = inventorySystem.getItems();
    vector<int> cartIndexMap;

    for (int i = 0; i < (int)items.size(); i++)
    {
        if (items[i].cartQty > 0)
            cartIndexMap.push_back(i);
    }

    if (cartIndexMap.empty())
    {
        cout << "\033[1;31mYour cart is empty!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\033[1;44m\033[1;97m========== REMOVE FROM CART ==========/\033[0m\n\n";

    cout << left << fixed << setprecision(2);
    cout << setw(5) << "No."
        << setw(25) << "Product"
        << setw(12) << "Qty"
        << setw(10) << "Price" << "\n";

    cout << "\033[1;33m" << string(52, '=') << "\033[0m\n";

    for (size_t i = 0; i < cartIndexMap.size(); i++)
    {
        int idx = cartIndexMap[i];
        cout << setw(5) << (i + 1)
            << setw(25) << items[idx].name
            << setw(12) << items[idx].cartQty
            << setw(10) << items[idx].price << "\n";
    }

    cout << "\n\033[1;36m0. Clear All Items\033[0m\n";
    cout << "\033[1;31m-1. Go Back\033[0m\n\n";

    cout << "\033[1;35mEnter product number to remove (or 0 to clear all, -1 to go back): \033[0m";
    int choice = Utils::getIntInput();

    if (choice == -1)
        return;

    if (choice == 0)
    {
        cout << "\n\033[1;33mAre you sure you want to remove ALL items? (y/n): \033[0m";
        if (Utils::getYesNoInput() == 'y')
        {
            for (int idx : cartIndexMap)
            {
                items[idx].cartQty = 0;
            }
            cout << "\033[1;32m\nCart cleared successfully!\033[0m\n";
            Utils::pressEnterToContinue();
        }
        return;
    }

    if (choice < 1 || choice >(int)cartIndexMap.size())
    {
        cout << "\033[1;31mInvalid selection!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    int itemIdx = cartIndexMap[choice - 1];
    Inventory& product = items[itemIdx];

    cout << "\n\033[1;33mEnter quantity to remove (Max: " << product.cartQty << "): \033[0m";
    int qtyToRemove = Utils::getIntInput();

    if (qtyToRemove <= 0 || qtyToRemove > product.cartQty)
    {
        cout << "\033[1;31mInvalid quantity!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    product.cartQty -= qtyToRemove;

    cout << "\033[1;32m\nRemoved successfully!\033[0m\n";
    cout << "\033[1;36mProduct: \033[0m" << product.name << "\n";
    cout << "\033[1;36mRemoved: \033[0m" << qtyToRemove << "\n";
    cout << "\033[1;36mRemaining: \033[0m" << product.cartQty << "\n";

    Utils::pressEnterToContinue();
}

bool CartManager::processPayment(double bill, double& codCharges)
{
    int choice;
    double amount;
    string number;
    bool valid;

    codCharges = 0;

    while (true)
    {
        cout << "\n\033[1;34m================== PAYMENT OPTIONS ==================\033[0m\n";
        cout << "\033[1;32mBill Amount: Rs. " << fixed << setprecision(2) << bill << "\033[0m\n\n";

        cout << "\033[1;36m1. Online Payment\n2. Cash on Delivery (COD)\n0. Go Back\033[0m\n";

        cout << "\033[1;33mEnter your choice: \033[0m";
        cin >> choice;

        if (choice == 1)
        {
            Utils::clearScreen();
            Utils::appTheme();

            cout << "\n\033[1;36m--- ONLINE PAYMENT ---\033[0m\n";
            cout << "\033[1;33mEnter 0 at any step to go back.\033[0m\n\n";

            while (true)
            {
                while (true)
                {
                    cout << "\033[1;33mEnter Mobile/Card Number (11 digits) or 0 to go back: \033[0m";
                    cin >> number;

                    if (number == "0")
                        return false;

                    valid = true;

                    if (number.length() != 11)
                        valid = false;
                    else
                    {
                        for (int i = 0; i < 11; i++)
                        {
                            if (number[i] < '0' || number[i] > '9')
                            {
                                valid = false;
                                break;
                            }
                        }
                    }

                    if (valid)
                        break;

                    cout << "\033[1;31mInvalid mobile number! Please enter exactly 11 digits.\033[0m\n";
                }

                cout << "\033[1;33mEnter Amount (Rs.) or 0 to go back: \033[0m";
                cin >> amount;

                if (amount == 0)
                    return false;

                if (amount == bill)
                {
                    cout << "\033[1;32mPayment Successful!\033[0m\n";
                    return true;
                }

                cout << "\033[1;31mWrong Amount! Please pay Rs. " << bill << "\033[0m\n";
                Utils::pressEnterToContinue();
                // loops to re-enter the online payment (again)
            }
        }
        else if (choice == 2)
        {
            codCharges = 100;

            cout << "\033[1;33m--- CASH ON DELIVERY ---\033[0m\n";
            cout << "\033[1;32mRs. 100 COD Charges Applied.\033[0m\n";
            cout << "\033[1;32mTotal Payable Amount: Rs. " << (bill + codCharges) << "\033[0m\n";

            return true;
        }
        else if (choice == 0)
        {
            cout << "\033[1;33mGoing Back.\033[0m\n";
            return false;
        }
        else
        {
            cout << "\033[1;31mInvalid option! Try again.\033[0m\n";
            Utils::pressEnterToContinue();
        }
    }
}

void CartManager::buyProduct()
{
    Utils::clearScreen();
    Utils::appTheme();

    auto& items = inventorySystem.getItems();

    inventorySystem.showCategories();

    int catChoice = Utils::getIntInput();

    if (catChoice == 0)
        return;

    string category = inventorySystem.getCategoryByChoice(catChoice);

    if (category == "")
    {
        cout << "\033[1;31mInvalid category!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    Utils::appTheme();

    cout << "\n\033[1;34m============ Products in " << category << " ============\033[0m\n";
    vector<int> indexMap;
    showCategoryProducts(items, category, indexMap);

    if (indexMap.empty())
    {
        cout << "\033[1;31mNo products found!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\n\033[1;36mSelect Product Number: \033[0m";
    int choice = Utils::getIntInput();

    if (choice < 1 || choice > (int)indexMap.size())
    {
        cout << "\033[1;31mInvalid choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    Inventory& p = items[indexMap[choice - 1]];

    cout << "\033[1;36mEnter Quantity: \033[0m";
    int qty = Utils::getIntInput();

    if (qty <= 0)
    {
        cout << "\033[1;31mInvalid quantity!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    if (qty > p.stock)
    {
        cout << "\033[1;31mNot enough stock!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    double bill = qty * p.price;
    double codCharges = 0;

    Utils::clearScreen();
    Utils::appTheme();

    printSingleProductBox(p, qty, "================ PURCHASE SUMMARY ================");

    if (!processPayment(bill, codCharges))
    {
        cout << "\033[1;31mPayment Failed!\033[0m\n";
        vector<string> productNames = { p.name };
        ::historySystem.logCancelPayment(::currentUsername, productNames, bill);
        Utils::pressEnterToContinue();
        return;
    }

    p.stock -= qty;

    vector<Inventory> temp;
    Inventory t = p;
    t.cartQty = qty;
    temp.push_back(t);

    salesSystem.recordSale(p.name, qty, bill);
    ::historySystem.logPurchase(::currentUsername, p.name, qty, bill);

    ReceiptManager::generateReceipt(temp, bill, codCharges);

    inventorySystem.saveInventoryToFile();
    salesSystem.saveSalesToFile();

    cout << "\033[1;32m\nPurchase Successful!\033[0m\n";
    printSingleProductBox(p, qty, "=================== FINAL BILL ===================");

    if (codCharges > 0)
    {
        cout << "\033[1;33mCOD Charges : \033[0mRs. " << codCharges << "\n";
        cout << "\033[1;32mGrand Total : \033[0mRs. " << (bill + codCharges) << "\n";
    }

    Utils::pressEnterToContinue();
}