#ifndef HISTORY_MANAGER_H
#define HISTORY_MANAGER_H

#include "Manager.h"
#include <vector>
#include <string>
#include <ctime>

class HistoryManager : public Manager
{
private:
    struct HistoryEntry
    {
        std::string timestamp;
        std::string action;
        std::string username;
        std::string productName;
        int quantity;
        double amount;

        bool operator==(const HistoryEntry& other) const
        {
            return this->timestamp == other.timestamp && 
                   this->username == other.username;
        }

        bool operator<(const HistoryEntry& other) const
        {
            return this->timestamp < other.timestamp;
        }
    };

    std::vector<HistoryEntry> history;

    std::string getCurrentTimestamp();

public:
    HistoryManager() : Manager("HistoryManager") {}

    void initialize() override;
    void save() override;
    void load() override;
    std::string getStatus() const override;

    void logAddToCart(const std::string& username, const std::string& productName, int quantity);
    void logRemoveFromCart(const std::string& username, const std::string& productName, int quantity);
    void logPurchase(const std::string& username, const std::string& productName, int quantity, double amount);
    void logCancelPayment(const std::string& username, const std::vector<std::string>& productNames, double totalAmount);

    void saveHistoryToFile();
    void loadHistoryFromFile();
    void showHistory();
    void showUserHistory(const std::string& username);


    template<typename Predicate>
    std::vector<HistoryEntry> filterHistory(Predicate pred)
    {
        std::vector<HistoryEntry> filtered;
        for (const auto& entry : history)
        {
            if (pred(entry))
                filtered.push_back(entry);
        }
        return filtered;
    }

    template<typename Predicate>
    int countHistoryIf(Predicate pred)
    {
        int count = 0;
        for (const auto& entry : history)
        {
            if (pred(entry))
                count++;
        }
        return count;
    }

    template<typename Predicate>
    double calculateTotalAmountIf(Predicate pred)
    {
        double total = 0.0;
        for (const auto& entry : history)
        {
            if (pred(entry))
                total += entry.amount;
        }
        return total;
    }

    std::vector<HistoryEntry> getUserActions(const std::string& username)
    {
        return filterHistory([username](const HistoryEntry& e) {
            return e.username == username;
        });
    }

    std::vector<HistoryEntry> getHistoryByAction(const std::string& action)
    {
        return filterHistory([action](const HistoryEntry& e) {
            return e.action == action;
        });
    }

    size_t getHistoryCount() const { return history.size(); }
};

#endif 