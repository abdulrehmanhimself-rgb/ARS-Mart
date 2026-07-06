#include "Inventory.h"
#include "InventoryManager.h"
#include "Globals.h"
#include "Utils.h"

#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>
#include <vector>
#include <limits>
#include <cctype>
#include <filesystem>
#include <stdexcept>

using namespace std;
namespace fs = std::filesystem;

bool InventoryManager::categoryExists(string cat)
{
    for (const auto& c : categories)
    {
        if (toLower(c) == toLower(cat))
            return true;
    }
    return false;
}

void InventoryManager::loadInventoryFromFile()
{
    fs::create_directories("data");

    ifstream file("data/Inventory.txt");

    if (!file)
    {
        cerr << "\033[1;31m\n[ERROR] Inventory file not found!\033[0m\n";
        items.clear();
        return;
    }

    items.clear();

    string line;

    while (getline(file, line))
    {
        if (line.empty())
            continue;

        vector<string> fields;
        string temp;

        for (size_t i = 0; i < line.length(); i++)
        {
            if (line[i] == '|')
            {
                fields.push_back(temp);
                temp.clear();
            }
            else
            {
                temp += line[i];
            }
        }
        fields.push_back(temp);

        if (fields.size() < 4 || fields.size() > 5)
        {
            cerr << "[SKIP] Invalid line: " << line << "\n";
            continue;
        }

        try
        {
            string name = fields[0];
            string category = fields[1];
            int stock = stoi(fields[2]);
            int price = stoi(fields[3]);
            int cartQty = (fields.size() == 5) ? stoi(fields[4]) : 0;

            if (name.empty() || category.empty() || stock < 0 || price < 0 || cartQty < 0)
            {
                cerr << "[SKIP] Invalid values in line: " << line << "\n";
                continue;
            }

            Inventory item(name, category, stock, price);
            item.cartQty = cartQty;
            items.push_back(item);
        }
        catch (const exception&)
        {
            cerr << "[SKIP] Parse error in line: " << line << "\n";
        }
    }

    file.close();
}

void InventoryManager::saveInventoryToFile()
{
    fs::create_directories("data");

    ofstream file("data/Inventory.txt");

    if (!file.is_open())
    {
        cerr << "[ERROR] Cannot open data/Inventory.txt for writing!\n";
        return;
    }

    for (const auto& item : items)
    {
        file << item.name << "|"
            << item.category << "|"
            << item.stock << "|"
            << item.price << "|"
            << item.cartQty << endl;
    }

    file.close();
}

void InventoryManager::loadCategoriesFromFile()
{
    fs::create_directories("data");

    ifstream file("data/categories.txt");

    if (!file)
    {
        categories = {
            "Electronics", "Grocery", "Stationary", "Medicines", "Sports",
            "Makeup", "Clothing", "Jewellery", "Home Appliances",
            "Fruits", "Vegetables", "Toys", "Wedding"
        };
        saveCategoriesToFile();
        return;
    }

    categories.clear();
    string line;

    while (getline(file, line))
    {
        if (!line.empty())
            categories.push_back(line);
    }

    file.close();

    if (categories.empty())
    {
        categories = {
            "Electronics", "Grocery", "Stationary", "Medicines", "Sports",
            "Makeup", "Clothing", "Jewellery", "Home Appliances",
            "Fruits", "Vegetables", "Toys", "Wedding"
        };
        saveCategoriesToFile();
    }
}

void InventoryManager::saveCategoriesToFile()
{
    fs::create_directories("data");

    ofstream file("data/categories.txt");

    if (!file.is_open())
    {
        cerr << "[ERROR] Cannot open data/categories.txt for writing!\n";
        return;
    }

    for (const auto& cat : categories)
    {
        file << cat << endl;
    }

    file.close();
}

string InventoryManager::toLower(string str)
{
    for (size_t i = 0; i < str.length(); i++)
    {
        str[i] = static_cast<char>(tolower(static_cast<unsigned char>(str[i])));
    }
    return str;
}

bool InventoryManager::productsExistInCategory(string cat)
{
    for (const auto& item : items)
    {
        if (toLower(item.category) == toLower(cat))
            return true;
    }
    return false;
}

string InventoryManager::getCategoryByChoice(int choice)
{
    if (choice < 1 || choice > static_cast<int>(categories.size()))
        return "";

    return categories[choice - 1];
}

void InventoryManager::showAllCategories()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;45m\033[1;97m"
        << "===================== ALL AVAILABLE CATEGORIES ====================="
        << "\033[0m\n\n";

    if (categories.empty())
    {
        cout << "\033[1;31mNo categories available.\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    for (size_t i = 0; i < categories.size(); i++)
    {
        cout << "\033[1;36m[ "
            << setw(2) << i + 1
            << " ] \033[1;93m"
            << setw(20) << left << categories[i]
            << "\033[0m";

        if ((i + 1) % 3 == 0)
            cout << endl;
    }

    cout << "\n\n\033[1;35m====================================================================\033[0m\n";
    Utils::pressEnterToContinue();
}

void InventoryManager::showCategories()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m"
        << "==================== AVAILABLE CATEGORIES ===================="
        << "\033[0m\n\n";

    if (categories.empty())
    {
        cout << "\033[1;31mNo categories available.\033[0m\n";
        cout << "\n\033[1;31m[ 0 ] Go Back\033[0m\n";
        cout << "\033[1;35m=============================================================\033[0m\n";
        return;
    }

    for (size_t i = 0; i < categories.size(); i++)
    {
        cout << "\033[1;36m[ "
            << setw(2) << i + 1
            << " ] \033[1;93m"
            << setw(22) << left << categories[i]
            << "\033[0m";

        if ((i + 1) % 2 == 0)
            cout << endl;
    }

    cout << "\n\n\033[1;31m[ 0 ] Go Back\033[0m\n";
    cout << "\033[1;35m=============================================================\033[0m\n";
}

void InventoryManager::addNewCategory()
{
    Utils::clearScreen();
    Utils::appTheme();

    string newCat = Utils::getStringInput("\033[1;36mEnter New Category Name: \033[0m");

    if (newCat.empty())
    {
        cout << "\033[1;31mCategory name cannot be empty!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    if (categoryExists(newCat))
    {
        cout << "\033[1;31mCategory already exists!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    categories.push_back(newCat);
    saveCategoriesToFile();

    cout << "\033[1;32mCategory Added Successfully!\033[0m\n";
    Utils::pressEnterToContinue();
}

void InventoryManager::deleteCategory()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m==================== DELETE CATEGORY ====================\033[0m\n\n";

    if (categories.empty())
    {
        cout << "\033[1;31mNo categories available!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    for (size_t i = 0; i < categories.size(); i++)
    {
        cout << "\033[1;36m[ " << i + 1 << " ] "
            << "\033[1;93m" << categories[i] << "\033[0m\n";
    }

    cout << "\n\033[1;31m[ 0 ] Cancel\033[0m\n";
    cout << "\n\033[1;32mSelect Category Number: \033[0m";

    int ch = Utils::getIntInput();

    if (ch == 0)
        return;

    if (ch < 1 || ch > static_cast<int>(categories.size()))
    {
        cout << "\033[1;31mInvalid choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    string cat = categories[ch - 1];

    if (productsExistInCategory(cat))
    {
        cout << "\n\033[1;31mCannot delete category!\033[0m\n";
        cout << "\033[1;33mProducts exist under this category.\033[0m\n";
        cout << "\033[1;36mPlease delete or move products first.\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    categories.erase(categories.begin() + (ch - 1));
    saveCategoriesToFile();

    cout << "\n\033[1;32mCategory Deleted Successfully!\033[0m\n";
    Utils::pressEnterToContinue();
}

void InventoryManager::searchProduct()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;34m\n=============== SMART PRODUCT SEARCH ===============\033[0m\n";

    string search = Utils::getStringInput("\033[1;35mStart typing product name: \033[0m");
    search = toLower(search);

    vector<int> matches;

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].name).find(search) != string::npos)
        {
            matches.push_back(static_cast<int>(i));
        }
    }

    if (matches.empty())
    {
        cout << "\033[1;31mNo matching products found!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\n\033[1;32mSuggestions:\033[0m\n";

    for (size_t i = 0; i < matches.size(); i++)
    {
        cout << "\033[1;36m" << i + 1 << ". \033[0m"
            << "\033[1;93m" << items[matches[i]].name << "\033[0m"
            << " (" << items[matches[i]].category << ")\n";
    }

    cout << "\n\033[1;33mSelect product number to view details (0 to cancel): \033[0m";

    int choice = Utils::getIntInput();

    if (choice <= 0 || choice > static_cast<int>(matches.size()))
        return;

    Inventory& p = items[matches[choice - 1]];

    cout << "\n\033[1;36mProduct Details:\033[0m\n";
    cout << "Name     : " << p.name << endl;
    cout << "Category : " << p.category << endl;
    cout << "Stock    : " << p.stock << endl;
    cout << "Price    : Rs. " << p.price << endl;

    Utils::pressEnterToContinue();
}

void InventoryManager::lowStockWarning()
{
    Utils::clearScreen();
    Utils::appTheme();

    const int LOW_STOCK_LIMIT = 5;
    bool found = false;

    cout << "\033[1;31m\n========== LOW STOCK ALERT ==========\033[0m\n";

    cout << left
        << "\033[1;33m" << setw(25) << "Product" << "\033[0m"
        << "\033[1;36m" << setw(15) << "Category" << "\033[0m"
        << "\033[1;32m" << setw(10) << "Stock" << "\033[0m\n";

    cout << "\033[1;35m-----------------------------------------------\033[0m\n";

    for (size_t i = 0; i < items.size(); i++)
    {
        if (items[i].stock <= LOW_STOCK_LIMIT)
        {
            cout << "\033[1;33m" << setw(25) << items[i].name << "\033[0m"
                << "\033[1;36m" << setw(15) << items[i].category << "\033[0m"
                << "\033[1;31m" << setw(10) << items[i].stock << "\033[0m\n";
            found = true;
        }
    }

    if (!found)
        cout << "\033[1;32mAll products have sufficient stock.\033[0m\n";

    Utils::pressEnterToContinue();
}

void InventoryManager::viewInventory()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m==================== VIEW INVENTORY ====================\033[0m\n\n";

    if (items.empty())
    {
        cout << "\033[1;31mNo products available in inventory!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    showCategories();
    cout << "\n\033[1;32mSelect Category Number: \033[0m";

    int ch = Utils::getIntInput();

    if (ch == 0)
        return;

    string selectedCategory = getCategoryByChoice(ch);

    if (selectedCategory.empty())
    {
        cout << "\033[1;31mInvalid category choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;45m\033[1;97m================ PRODUCTS IN "
        << selectedCategory
        << " ================\033[0m\n\n";

    cout << left
        << setw(5) << "No"
        << setw(25) << "Product"
        << setw(20) << "Category"
        << setw(10) << "Stock"
        << setw(10) << "Price" << endl;

    cout << "------------------------------------------------------------------\n";

    bool found = false;
    int count = 1;

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].category) == toLower(selectedCategory))
        {
            cout << left
                << setw(5) << count++
                << setw(25) << items[i].name
                << setw(20) << items[i].category
                << setw(10) << items[i].stock
                << setw(10) << items[i].price << endl;
            found = true;
        }
    }

    if (!found)
    {
        cout << "\033[1;31mNo products found in this category!\033[0m\n";
    }

    Utils::pressEnterToContinue();
}

void InventoryManager::showAllStockByCategory()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;46m\033[1;30m==================== FULL INVENTORY ====================\033[0m\n\n";

    if (items.empty())
    {
        cout << "\033[1;31mInventory is empty!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    for (size_t c = 0; c < categories.size(); c++)
    {
        cout << "\n\033[1;44m\033[1;97mCategory: " << categories[c] << "\033[0m\n";

        cout << left
            << setw(5) << "No"
            << setw(25) << "Product"
            << setw(10) << "Stock"
            << setw(10) << "Price" << endl;

        cout << "--------------------------------------------------\n";

        bool found = false;
        int count = 1;

        for (size_t i = 0; i < items.size(); i++)
        {
            if (toLower(items[i].category) == toLower(categories[c]))
            {
                cout << left
                    << setw(5) << count++
                    << setw(25) << items[i].name
                    << setw(10) << items[i].stock
                    << setw(10) << items[i].price << endl;
                found = true;
            }
        }

        if (!found)
        {
            cout << "No products in this category.\n";
        }
    }

    Utils::pressEnterToContinue();
}

void InventoryManager::addNewProduct()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m==================== ADD NEW PRODUCT ====================\033[0m\n\n";

    if (categories.empty())
    {
        cout << "\033[1;31mNo categories available! Add a category first.\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    showCategories();
    cout << "\n\033[1;32mSelect Category Number: \033[0m";
    int ch = Utils::getIntInput();

    if (ch == 0)
        return;

    string selectedCategory = getCategoryByChoice(ch);

    if (selectedCategory.empty())
    {
        cout << "\033[1;31mInvalid category choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    string productName = Utils::getStringInput("\033[1;36mEnter Product Name: \033[0m");

    cout << "\033[1;36mEnter Stock Quantity: \033[0m";
    int stock = Utils::getIntInput();

    cout << "\033[1;36mEnter Product Price: \033[0m";
    int price = Utils::getIntInput();

    if (productName.empty())
    {
        cout << "\033[1;31mProduct name cannot be empty!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    if (stock < 0 || price < 0)
    {
        cout << "\033[1;31mStock and price cannot be negative!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].name) == toLower(productName))
        {
            cout << "\033[1;31mProduct already exists!\033[0m\n";
            Utils::pressEnterToContinue();
            return;
        }
    }

    Inventory newItem(productName, selectedCategory, stock, price);
    items.push_back(newItem);
    saveInventoryToFile();

    cout << "\033[1;32mProduct added successfully!\033[0m\n";
    Utils::pressEnterToContinue();
}

void InventoryManager::deleteProduct()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;41m\033[1;97m==================== DELETE PRODUCT ====================\033[0m\n\n";

    if (items.empty())
    {
        cout << "\033[1;31mNo products available to delete!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    string search = Utils::getStringInput("\033[1;36mEnter product name to delete: \033[0m");
    vector<int> matches;

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].name).find(toLower(search)) != string::npos)
        {
            matches.push_back(static_cast<int>(i));
        }
    }

    if (matches.empty())
    {
        cout << "\033[1;31mNo matching product found!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\n\033[1;33mMatching Products:\033[0m\n";
    for (size_t i = 0; i < matches.size(); i++)
    {
        cout << i + 1 << ". "
            << items[matches[i]].name
            << " (" << items[matches[i]].category << ")\n";
    }

    cout << "\n\033[1;32mSelect product number to delete (0 to cancel): \033[0m";
    int choice = Utils::getIntInput();

    if (choice == 0)
        return;

    if (choice < 1 || choice > static_cast<int>(matches.size()))
    {
        cout << "\033[1;31mInvalid choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    int index = matches[choice - 1];
    cout << "\033[1;31mDeleting: " << items[index].name << "\033[0m\n";

    items.erase(items.begin() + index);
    saveInventoryToFile();

    cout << "\033[1;32mProduct deleted successfully!\033[0m\n";
    Utils::pressEnterToContinue();
}

void InventoryManager::restockProduct()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m==================== RESTOCK PRODUCT ====================\033[0m\n\n";

    if (items.empty())
    {
        cout << "\033[1;31mNo products available to restock!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    string search = Utils::getStringInput("\033[1;36mEnter product name to restock: \033[0m");
    vector<int> matches;

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].name).find(toLower(search)) != string::npos)
        {
            matches.push_back(static_cast<int>(i));
        }
    }

    if (matches.empty())
    {
        cout << "\033[1;31mNo matching product found!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\n\033[1;33mMatching Products:\033[0m\n";
    for (size_t i = 0; i < matches.size(); i++)
    {
        cout << i + 1 << ". "
            << items[matches[i]].name
            << " | Stock: " << items[matches[i]].stock
            << " | Price: " << items[matches[i]].price << "\n";
    }

    cout << "\n\033[1;32mSelect product number (0 to cancel): \033[0m";
    int choice = Utils::getIntInput();

    if (choice == 0)
        return;

    if (choice < 1 || choice > static_cast<int>(matches.size()))
    {
        cout << "\033[1;31mInvalid choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    int index = matches[choice - 1];

    cout << "\033[1;36mEnter quantity to add: \033[0m";
    int qty = Utils::getIntInput();

    if (qty <= 0)
    {
        cout << "\033[1;31mInvalid quantity!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    items[index].stock += qty;
    saveInventoryToFile();

    cout << "\033[1;32mStock updated successfully!\033[0m\n";
    cout << "New stock of " << items[index].name << ": " << items[index].stock << endl;

    Utils::pressEnterToContinue();
}

void InventoryManager::updateProductPrice()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;44m\033[1;97m==================== UPDATE PRODUCT PRICE ====================\033[0m\n\n";

    if (items.empty())
    {
        cout << "\033[1;31mNo products available!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    string search = Utils::getStringInput("\033[1;36mEnter product name to update price: \033[0m");
    vector<int> matches;

    for (size_t i = 0; i < items.size(); i++)
    {
        if (toLower(items[i].name).find(toLower(search)) != string::npos)
        {
            matches.push_back(static_cast<int>(i));
        }
    }

    if (matches.empty())
    {
        cout << "\033[1;31mNo matching product found!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << "\n\033[1;33mMatching Products:\033[0m\n";
    for (size_t i = 0; i < matches.size(); i++)
    {
        cout << i + 1 << ". "
            << items[matches[i]].name
            << " | Current Price: " << items[matches[i]].price << "\n";
    }

    cout << "\n\033[1;32mSelect product number (0 to cancel): \033[0m";
    int choice = Utils::getIntInput();

    if (choice == 0)
        return;

    if (choice < 1 || choice > static_cast<int>(matches.size()))
    {
        cout << "\033[1;31mInvalid choice!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    int index = matches[choice - 1];

    cout << "\033[1;36mEnter new price: \033[0m";
    int newPrice = Utils::getIntInput();

    if (newPrice < 0)
    {
        cout << "\033[1;31mPrice cannot be negative!\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    items[index].price = newPrice;
    saveInventoryToFile();

    cout << "\033[1;32mPrice updated successfully!\033[0m\n";
    cout << "New price of " << items[index].name << ": Rs. " << items[index].price << endl;

    Utils::pressEnterToContinue();
}

vector<Inventory>& InventoryManager::getItems()
{
    return items;
}

vector<string>& InventoryManager::getCategories()
{
    return categories;
}

void InventoryManager::initialize()
{
    loadInventoryFromFile();
    loadCategoriesFromFile();
    isInitialized = true;
}

void InventoryManager::save()
{
    saveInventoryToFile();
    saveCategoriesToFile();
}

void InventoryManager::load()
{
    loadInventoryFromFile();
    loadCategoriesFromFile();
}

string InventoryManager::getStatus() const
{
    return "InventoryManager - Items: " + to_string(items.size()) +
        ", Categories: " + to_string(categories.size());
}
