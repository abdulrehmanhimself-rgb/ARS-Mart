#ifndef INVENTORY_MANAGER_H
#define INVENTORY_MANAGER_H

#include "Inventory.h"
#include "Manager.h"
#include <vector>
#include <string>
#include <algorithm>
#include <iterator>


class InventoryManager : public Manager
{
private:
    std::vector<Inventory> items;
    std::vector<std::string> categories;

    template<typename T>
    bool saveToFile(const std::string& filename, const std::vector<T>& data);

    template<typename T>
    bool loadFromFile(const std::string& filename, std::vector<T>& data);

public:
    InventoryManager() : Manager("InventoryManager") {}

    void initialize() override;
    void save() override;
    void load() override;
    std::string getStatus() const override;

    void loadInventoryFromFile();
    void saveInventoryToFile();
    void loadCategoriesFromFile();
    void saveCategoriesToFile();
    void viewInventory();
    void showAllStockByCategory();
    void showAllCategories();
    void showCategories();
    void addNewProduct();
    void deleteProduct();
    void addNewCategory();
    void deleteCategory();
    void restockProduct();
    void updateProductPrice();
    void searchProduct();
    void lowStockWarning();

    std::string getCategoryByChoice(int ch);
    bool categoryExists(std::string cat);
    bool productsExistInCategory(std::string cat);
    std::string toLower(std::string str);

    std::vector<Inventory>& getItems();
    std::vector<std::string>& getCategories();


    template<typename Predicate>
    std::vector<Inventory> searchItems(Predicate pred)
    {
        std::vector<Inventory> results;
        for (const auto& item : items)
        {
            if (pred(item))
                results.push_back(item);
        }
        return results;
    }

    template<typename Comparator>
    void sortItems(Comparator comp)
    {
        std::sort(items.begin(), items.end(), comp);
    }

    template<typename Predicate>
    Inventory* findItem(Predicate pred)
    {
        for (auto& item : items)
        {
            if (pred(item))
                return &item;
        }
        return nullptr;
    }

    template<typename Predicate, typename UpdateFunc>
    int updateItemsIf(Predicate condition, UpdateFunc update)
    {
        int count = 0;
        for (auto& item : items)
        {
            if (condition(item))
            {
                update(item);
                count++;
            }
        }
        return count;
    }

    template<typename Predicate>
    std::vector<Inventory> filterItems(Predicate pred)
    {
        std::vector<Inventory> filtered;
        std::copy_if(items.begin(), items.end(), 
                  back_inserter(filtered), pred);
        return filtered;
    }
};

#endif
