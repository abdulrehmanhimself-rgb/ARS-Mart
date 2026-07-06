// SalesManager.h - ENHANCED WITH OOP CONCEPTS
#ifndef SALES_MANAGER_H
#define SALES_MANAGER_H

#include "Inventory.h"
#include "Manager.h"
#include <vector>
#include <string>

class SalesManager : public Manager
{
private:
    std::vector<std::string> salesDate;
    std::vector<std::string> salesProduct;
    std::vector<int> salesQty;
    std::vector<double> salesAmount;

    template<typename T, typename Aggregator>
    T aggregateSalesData(Aggregator func)
    {
        T result = T();
        for (size_t i = 0; i < salesAmount.size(); ++i)
        {
            result = func(result, salesAmount[i]);
        }
        return result;
    }

public:
    SalesManager() : Manager("SalesManager") {}

    void initialize() override;
    void save() override;
    void load() override;
    std::string getStatus() const override;

    void recordSale(std::string name, int qty, double amount);
    void saveSalesToFile();
    void dailySalesReport();
    void categoryWiseSalesReport(const std::vector<Inventory>& items,
        const std::vector<std::string>& categories);
    void productWiseProfitReport(const std::vector<Inventory>& items);


    template<typename Predicate>
    std::vector<double> filterSalesBy(Predicate pred)
    {
        std::vector<double> filtered;
        for (size_t i = 0; i < salesDate.size(); ++i)
        {
            if (pred(salesDate[i], salesAmount[i]))
                filtered.push_back(salesAmount[i]);
        }
        return filtered;
    }

    template<typename Predicate>
    double calculateTotalSalesIf(Predicate pred)
    {
        double total = 0.0;
        for (size_t i = 0; i < salesAmount.size(); ++i)
        {
            if (pred(salesProduct[i], salesAmount[i]))
                total += salesAmount[i];
        }
        return total;
    }

    double getHighestSale() const
    {
        if (salesAmount.empty()) return 0.0;
        double highest = salesAmount[0];
        for (const auto& amount : salesAmount)
        {
            if (amount > highest)
                highest = amount;
        }
        return highest;
    }

    double getLowestSale() const
    {
        if (salesAmount.empty()) return 0.0;
        double lowest = salesAmount[0];
        for (const auto& amount : salesAmount)
        {
            if (amount < lowest)
                lowest = amount;
        }
        return lowest;
    }

    double getTotalSales() const
    {
        double total = 0.0;
        for (const auto& amount : salesAmount)
            total += amount;
        return total;
    }

    double getAverageSale() const
    {
        if (salesAmount.empty()) return 0.0;
        return getTotalSales() / salesAmount.size();
    }

    size_t getSalesCount() const { return salesAmount.size(); }
};

#endif 
