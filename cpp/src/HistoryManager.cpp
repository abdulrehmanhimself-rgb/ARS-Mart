#include "HistoryManager.h"
#include <iostream>
#include <fstream>
#include <iomanip>
#include <sstream>
#include <ctime>
#include "Utils.h"

using namespace std;

string HistoryManager::getCurrentTimestamp()
{
    time_t now = time(0);
    tm ltm;
    localtime_s(&ltm, &now);

    stringstream ss;
    ss << 1900 + ltm.tm_year << "-"
       << setfill('0') << setw(2) << 1 + ltm.tm_mon << "-"
       << setfill('0') << setw(2) << ltm.tm_mday << " "
       << setfill('0') << setw(2) << ltm.tm_hour << ":"
       << setfill('0') << setw(2) << ltm.tm_min << ":"
       << setfill('0') << setw(2) << ltm.tm_sec;
    return ss.str();
}

void HistoryManager::logAddToCart(const string& username, const string& productName, int quantity)
{
    HistoryEntry entry;
    entry.timestamp = getCurrentTimestamp();
    entry.action = "ADD_TO_CART";
    entry.username = username;
    entry.productName = productName;
    entry.quantity = quantity;
    entry.amount = 0.0;

    history.push_back(entry);
    saveHistoryToFile();
}

void HistoryManager::logRemoveFromCart(const string& username, const string& productName, int quantity)
{
    HistoryEntry entry;
    entry.timestamp = getCurrentTimestamp();
    entry.action = "REMOVE_FROM_CART";
    entry.username = username;
    entry.productName = productName;
    entry.quantity = quantity;
    entry.amount = 0.0;

    history.push_back(entry);
    saveHistoryToFile();
}

void HistoryManager::logPurchase(const string& username, const string& productName, int quantity, double amount)
{
    HistoryEntry entry;
    entry.timestamp = getCurrentTimestamp();
    entry.action = "PURCHASE";
    entry.username = username;
    entry.productName = productName;
    entry.quantity = quantity;
    entry.amount = amount;

    history.push_back(entry);
    saveHistoryToFile();
}

void HistoryManager::logCancelPayment(const string& username, const vector<string>& productNames, double totalAmount)
{
    for (const auto& productName : productNames)
    {
        HistoryEntry entry;
        entry.timestamp = getCurrentTimestamp();
        entry.action = "CANCEL_PAYMENT";
        entry.username = username;
        entry.productName = productName;
        entry.quantity = 0; // Not applicable for cancel
        entry.amount = totalAmount / productNames.size(); // Split amount

        history.push_back(entry);
    }
    saveHistoryToFile();
}

void HistoryManager::saveHistoryToFile()
{
    ofstream file("data/history.txt", ios::app);
    if (!file.is_open()) return;

    if (!history.empty())
    {
        const auto& entry = history.back();
        file << entry.timestamp << "|"
             << entry.action << "|"
             << entry.username << "|"
             << entry.productName << "|"
             << entry.quantity << "|"
             << fixed << setprecision(2) << entry.amount << endl;
    }

    file.close();
}

void HistoryManager::loadHistoryFromFile()
{
    ifstream file("data/history.txt");
    if (!file.is_open()) return;

    string line;
    history.clear();

    while (getline(file, line))
    {
        stringstream ss(line);
        string token;
        HistoryEntry entry;

        getline(ss, entry.timestamp, '|');
        getline(ss, entry.action, '|');
        getline(ss, entry.username, '|');
        getline(ss, entry.productName, '|');
        getline(ss, token, '|');
        entry.quantity = stoi(token);
        getline(ss, token);
        entry.amount = stod(token);

        history.push_back(entry);
    }

    file.close();
}

void HistoryManager::showHistory()
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;36m=================== PURCHASE HISTORY ===================\033[0m\n\n";

    if (history.empty())
    {
        cout << "\033[1;33mNo history available.\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << left << setw(12) << "Date"
         << setw(10) << "Time"
         << setw(15) << "Action"
         << setw(15) << "Username"
         << setw(20) << "Product"
         << setw(8) << "Qty"
         << setw(10) << "Amount" << endl;

    cout << string(90, '-') << endl;

    for (const auto& entry : history)
    {
        size_t spacePos = entry.timestamp.find(' ');
        string date = (spacePos != string::npos) ? entry.timestamp.substr(0, spacePos) : entry.timestamp;
        string time = (spacePos != string::npos) ? entry.timestamp.substr(spacePos + 1) : "";

        cout << left << setw(12) << date
             << setw(10) << time
             << setw(15) << entry.action
             << setw(15) << entry.username
             << setw(20) << entry.productName
             << setw(8) << entry.quantity
             << fixed << setprecision(2) << setw(10) << entry.amount << endl;
    }

    cout << "\n\033[1;32mTotal Records: " << history.size() << "\033[0m\n";
    Utils::pressEnterToContinue();
}

void HistoryManager::showUserHistory(const string& username)
{
    Utils::clearScreen();
    Utils::appTheme();

    cout << "\033[1;36m=================== USER HISTORY: " << username << " ===================\033[0m\n\n";

    vector<HistoryEntry> userHistory;
    for (const auto& entry : history)
    {
        if (entry.username == username)
            userHistory.push_back(entry);
    }

    if (userHistory.empty())
    {
        cout << "\033[1;33mNo history found for this user.\033[0m\n";
        Utils::pressEnterToContinue();
        return;
    }

    cout << left << setw(12) << "Date"
         << setw(10) << "Time"
         << setw(15) << "Action"
         << setw(20) << "Product"
         << setw(8) << "Qty"
         << setw(10) << "Amount" << endl;

    cout << string(75, '-') << endl;

    for (const auto& entry : userHistory)
    {
        size_t spacePos = entry.timestamp.find(' ');
        string date = (spacePos != string::npos) ? entry.timestamp.substr(0, spacePos) : entry.timestamp;
        string time = (spacePos != string::npos) ? entry.timestamp.substr(spacePos + 1) : "";

        cout << left << setw(12) << date
             << setw(10) << time
             << setw(15) << entry.action
             << setw(20) << entry.productName
             << setw(8) << entry.quantity
             << fixed << setprecision(2) << setw(10) << entry.amount << endl;
    }

    cout << "\n\033[1;32mTotal Records: " << userHistory.size() << "\033[0m\n";
    Utils::pressEnterToContinue();
}

void HistoryManager::initialize()
{
    loadHistoryFromFile();
    isInitialized = true;
}

void HistoryManager::save()
{
    saveHistoryToFile();
}

void HistoryManager::load()
{
    loadHistoryFromFile();
}

std::string HistoryManager::getStatus() const
{
    return "HistoryManager - Total Entries: " + std::to_string(history.size());
}