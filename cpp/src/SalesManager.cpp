// SalesManager.cpp - CORRECTED
#include "SalesManager.h"
#include "Globals.h"
#include "Utils.h"
#include <iostream>
#include <fstream>
#include <iomanip>
#include <ctime>
#include <map>
#include <string>

using namespace std;

void SalesManager::recordSale(string name, int qty, double amount)
{
    time_t now = time(NULL);
    tm ltm;
    localtime_s(&ltm, &now);
    std::string date_str;
    if (&ltm != nullptr) {
        char date_buf[20];
        std::strftime(date_buf, sizeof(date_buf), "%Y-%m-%d", &ltm);
        date_str = date_buf;
    } else {
        date_str = "1970-01-01"; 
    }

    salesDate.push_back(date_str.c_str());
    salesProduct.push_back(name);
    salesQty.push_back(qty);
    salesAmount.push_back(amount);
}

void SalesManager::saveSalesToFile()
{
    ofstream file("data/sales.txt");

    for (int i = 0; i < salesDate.size(); i++)
    {
        file << salesDate[i] << "|"
            << salesProduct[i] << "|"
            << salesQty[i] << "|"
            << salesAmount[i] << endl;
    }

    file.close();
}

void SalesManager::dailySalesReport()
{
    Utils::clearScreen();  
    Utils::appTheme();    

    cout << "\n========== DAILY SALES REPORT ==========\n\n";

    cout << left
        << setw(15) << "Date"
        << setw(25) << "Product"
        << setw(10) << "Qty"
        << setw(10) << "Amount" << endl;

    cout << "--------------------------------------------------\n";

    if (salesDate.empty())
    {
        cout << "No sales recorded.\n";
        Utils::pressEnterToContinue(); 
        return;
    }

    for (int i = 0; i < salesDate.size(); i++)
    {
        cout << left
            << setw(15) << salesDate[i]
            << setw(25) << salesProduct[i]
            << setw(10) << salesQty[i]
            << setw(10) << salesAmount[i] << endl;
    }

    Utils::pressEnterToContinue(); 
}

void SalesManager::categoryWiseSalesReport(const vector<Inventory>& items,
    const vector<string>& categories)
{
    Utils::clearScreen(); 
    Utils::appTheme(); 

    cout << "\n====== CATEGORY WISE SALES REPORT ======\n\n";

    cout << left
        << setw(20) << "Category"
        << setw(15) << "Units Sold"
        << setw(15) << "Revenue" << endl;

    cout << "--------------------------------------------------\n";

    bool anySales = false;

    for (auto& cat : categories)
    {
        int totalQty = 0;
        double revenue = 0.0;

        // Use sales data instead of cartQty
        for (size_t i = 0; i < salesProduct.size(); ++i)
        {
            // Find the category of the sold product
            for (auto& item : items)
            {
                if (item.name == salesProduct[i] && item.category == cat)
                {
                    totalQty += salesQty[i];
                    revenue += salesAmount[i] * 100; // to int rupees
                    break;
                }
            }
        }

        if (totalQty > 0)
        {
            anySales = true;

            cout << left
                << setw(20) << cat
                << setw(15) << totalQty
                << setw(15) << revenue << endl;
        }
    }

    if (!anySales)
        cout << "No sales recorded yet.\n";

    Utils::pressEnterToContinue();  
}

void SalesManager::productWiseProfitReport(const vector<Inventory>& items)  
{
    Utils::clearScreen();  
    Utils::appTheme();     

    cout << "\n====== PRODUCT PROFIT REPORT ======\n\n";

    cout << left
        << setw(25) << "Product"
        << setw(10) << "Sold"
        << setw(10) << "Profit" << endl;

    cout << "---------------------------------------------\n";

    bool found = false;

    // Group sales by product
    map<string, int> productSales;
    for (size_t i = 0; i < salesProduct.size(); ++i)
    {
        productSales[salesProduct[i]] += salesQty[i];
    }

    for (auto& sale : productSales)
    {
        // Find the item to get cost
        for (auto& item : items)
        {
            if (item.name == sale.first)
            {
                found = true;
                int profit = sale.second * (item.price - item.cost);

                cout << left
                    << setw(25) << item.name
                    << setw(10) << sale.second
                    << setw(10) << profit << endl;
                break;
            }
        }
    }

    if (!found)
        cout << "No profit data available.\n";

    Utils::pressEnterToContinue();  
}


void SalesManager::initialize()
{
    isInitialized = true;
}

void SalesManager::save()
{
    saveSalesToFile();
}

void SalesManager::load()
{
    // 
}

std::string SalesManager::getStatus() const
{
    return "SalesManager - Total Sales: " + std::to_string(salesAmount.size()) +
           ", Total Revenue: Rs." + std::to_string(getTotalSales());
}
